package com.example.project.serviceimpl;

import com.example.project.dto.BookingDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.BookingService;
import com.example.project.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CompanyRepository companyRepository;
    private final CompanyLocationRepository locationRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final SiteInspectionRepository siteInspectionRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              CartRepository cartRepository,
                              CartItemRepository cartItemRepository,
                              CompanyRepository companyRepository,
                              CompanyLocationRepository locationRepository,
                              UserRepository userRepository,
                              InventoryService inventoryService,
                              SiteInspectionRepository siteInspectionRepository) {
        this.bookingRepository = bookingRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.companyRepository = companyRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
        this.siteInspectionRepository = siteInspectionRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void validateBookingAccess(Booking booking, User user) {
        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_WORKER) {
            return;
        }
        if (!booking.getUser().getId().equals(user.getId()) &&
            !booking.getCompany().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot access bookings belonging to another company.");
        }
    }

    @Override
    public BookingDto.Response createBooking(BookingDto.CreateRequest request, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

        if (user.getRole() != Role.ROLE_ADMIN && !company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot create a booking for another company.");
        }

        CompanyLocation location = null;
        if (request.getCompanyLocationId() != null) {
            location = locationRepository.findById(request.getCompanyLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + request.getCompanyLocationId()));
        }

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User cart is empty"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Please add items to cart before booking.");
        }

        // Validate inventory and reserve stock
        for (CartItem item : cart.getItems()) {
            boolean reserved = inventoryService.reserveStock(item.getProduct().getId(), item.getQuantity());
            if (!reserved) {
                throw new IllegalStateException("Insufficient inventory for product: " + item.getProduct().getName());
            }
        }

        Booking booking = new Booking();
        booking.setBookingNumber("BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setUser(user);
        booking.setCompany(company);
        booking.setCompanyLocation(location);
        booking.setInstallationDate(request.getInstallationDate());
        booking.setNotes(request.getNotes());
        booking.setStatus(request.isRequireInspection() ?
                Booking.BookingStatus.SITE_INSPECTION_REQUIRED : Booking.BookingStatus.PENDING_PAYMENT);

        BigDecimal plantCharges = BigDecimal.ZERO;
        BigDecimal installationCharges = BigDecimal.ZERO;
        BigDecimal maintenanceCharges = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            BigDecimal unitPrice = ci.isRental() ? ci.getProduct().getRentalPrice() : ci.getProduct().getPrice();
            BigDecimal installCharge = ci.isIncludeInstallation() ? ci.getProduct().getInstallationCharge() : BigDecimal.ZERO;
            BigDecimal maintCharge = ci.getMaintenancePlan() != null ? ci.getMaintenancePlan().getMonthlyPrice() : BigDecimal.ZERO;

            if (ci.getMaintenancePlan() != null) {
                booking.setMaintenancePlan(ci.getMaintenancePlan());
            }

            BigDecimal itemTotal = (unitPrice.add(installCharge).add(maintCharge)).multiply(BigDecimal.valueOf(ci.getQuantity()));

            BookingItem bi = new BookingItem(booking, ci.getProduct(), ci.getQuantity(), unitPrice, installCharge, maintCharge, itemTotal, ci.isRental());
            booking.addItem(bi);

            plantCharges = plantCharges.add(unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity())));
            installationCharges = installationCharges.add(installCharge.multiply(BigDecimal.valueOf(ci.getQuantity())));
            maintenanceCharges = maintenanceCharges.add(maintCharge.multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        BigDecimal subtotal = plantCharges.add(installationCharges).add(maintenanceCharges);
        BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal grandTotal = subtotal.add(taxAmount).subtract(discount);

        booking.setPlantCharges(plantCharges);
        booking.setInstallationCharges(installationCharges);
        booking.setMaintenanceCharges(maintenanceCharges);
        booking.setTaxAmount(taxAmount);
        booking.setDiscountAmount(discount);
        booking.setGrandTotal(grandTotal);

        Booking saved = bookingRepository.save(booking);

        if (request.isRequireInspection()) {
            SiteInspection inspection = new SiteInspection();
            inspection.setBooking(saved);
            inspection.setStatus(SiteInspection.InspectionStatus.REQUESTED);
            siteInspectionRepository.save(inspection);
        }

        // Clear user cart
        cart.getItems().clear();
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);

        return mapToBookingResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto.Response getBookingById(Long id, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        validateBookingAccess(booking, user);
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingDto.Response> getBookings(String userEmail, Pageable pageable) {
        User user = getUser(userEmail);
        Page<Booking> bookings;
        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_WORKER) {
            bookings = bookingRepository.findAll(pageable);
        } else {
            bookings = bookingRepository.findByUserId(user.getId(), pageable);
        }
        return bookings.map(this::mapToBookingResponse);
    }

    @Override
    public BookingDto.Response updateBookingStatus(Long id, BookingDto.StatusUpdateRequest request, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        Booking.BookingStatus nextStatus = Booking.BookingStatus.valueOf(request.getStatus().toUpperCase());
        validateStatusTransition(booking.getStatus(), nextStatus, user.getRole());

        booking.setStatus(nextStatus);
        booking.setUpdatedAt(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);

        return mapToBookingResponse(saved);
    }

    @Override
    public BookingDto.Response cancelBooking(Long id, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        validateBookingAccess(booking, user);

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel booking in status: " + booking.getStatus());
        }

        // Release inventory if not yet installed
        if (booking.getStatus() != Booking.BookingStatus.INSTALLATION_COMPLETED &&
            booking.getStatus() != Booking.BookingStatus.MAINTENANCE_ACTIVE) {
            for (BookingItem item : booking.getItems()) {
                inventoryService.releaseStock(item.getProduct().getId(), item.getQuantity());
            }
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);

        return mapToBookingResponse(saved);
    }

    private void validateStatusTransition(Booking.BookingStatus current, Booking.BookingStatus next, Role role) {
        if (role != Role.ROLE_ADMIN && role != Role.ROLE_WORKER) {
            if (next != Booking.BookingStatus.CANCELLED) {
                throw new AccessDeniedException("Only admins and workers can change booking status");
            }
        }
    }

    private BookingDto.Response mapToBookingResponse(Booking booking) {
        BookingDto.Response resp = new BookingDto.Response();
        resp.setId(booking.getId());
        resp.setBookingNumber(booking.getBookingNumber());
        resp.setUserId(booking.getUser().getId());
        resp.setCompanyId(booking.getCompany().getId());
        resp.setCompanyName(booking.getCompany().getName());
        if (booking.getCompanyLocation() != null) {
            resp.setCompanyLocationId(booking.getCompanyLocation().getId());
            resp.setLocationName(booking.getCompanyLocation().getLocationName());
        }
        resp.setStatus(booking.getStatus().name());
        resp.setPaymentStatus(booking.getPaymentStatus().name());
        resp.setPlantCharges(booking.getPlantCharges());
        resp.setInstallationCharges(booking.getInstallationCharges());
        resp.setMaintenanceCharges(booking.getMaintenanceCharges());
        resp.setTaxAmount(booking.getTaxAmount());
        resp.setDiscountAmount(booking.getDiscountAmount());
        resp.setGrandTotal(booking.getGrandTotal());
        resp.setInstallationDate(booking.getInstallationDate());
        if (booking.getMaintenancePlan() != null) {
            resp.setMaintenancePlanId(booking.getMaintenancePlan().getId());
            resp.setMaintenancePlanName(booking.getMaintenancePlan().getName());
        }
        resp.setNotes(booking.getNotes());
        resp.setCreatedAt(booking.getCreatedAt());

        List<BookingDto.ItemResponse> itemResponses = booking.getItems().stream().map(bi -> {
            BookingDto.ItemResponse itemDto = new BookingDto.ItemResponse();
            itemDto.setId(bi.getId());
            itemDto.setProductId(bi.getProduct().getId());
            itemDto.setProductName(bi.getProduct().getName());
            itemDto.setQuantity(bi.getQuantity());
            itemDto.setRental(bi.isRental());
            itemDto.setUnitPrice(bi.getUnitPrice());
            itemDto.setInstallationCharge(bi.getInstallationCharge());
            itemDto.setMaintenanceCharge(bi.getMaintenanceCharge());
            itemDto.setTotalPrice(bi.getTotalPrice());
            return itemDto;
        }).collect(Collectors.toList());

        resp.setItems(itemResponses);
        return resp;
    }
}
