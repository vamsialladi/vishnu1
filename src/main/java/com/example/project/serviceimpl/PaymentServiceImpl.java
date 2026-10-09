package com.example.project.serviceimpl;

import com.example.project.dto.PaymentDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.PaymentGateway;
import com.example.project.service.PaymentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentGateway paymentGateway;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InvoiceRepository invoiceRepository,
                              BookingRepository bookingRepository,
                              UserRepository userRepository,
                              PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.paymentGateway = paymentGateway;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void validateBookingAccess(Booking booking, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) return;
        if (!booking.getUser().getId().equals(user.getId()) &&
            !booking.getCompany().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot view/pay for another user's booking.");
        }
    }

    @Override
    public PaymentDto.Response initiateAndProcessPayment(PaymentDto.ProcessRequest request, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + request.getBookingId()));

        validateBookingAccess(booking, user);

        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionId(txId);
        payment.setAmount(booking.getGrandTotal());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("INITIATED");

        // Process through gateway
        PaymentGateway.PaymentGatewayResult result = paymentGateway.processPayment(
                txId, booking.getGrandTotal(), "INR", request.getPaymentMethod());

        if (result.isSuccess()) {
            payment.setStatus("SUCCESS");
            payment.setGatewayReference(result.getGatewayReference());

            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
            if (booking.getStatus() == Booking.BookingStatus.PENDING_PAYMENT) {
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
            }
            bookingRepository.save(booking);

            // Generate Invoice
            generateInvoiceForBooking(booking);
        } else {
            payment.setStatus("FAILED");
        }

        Payment saved = paymentRepository.save(payment);
        return mapToPaymentResponse(saved);
    }

    private Invoice generateInvoiceForBooking(Booking booking) {
        return invoiceRepository.findByBookingId(booking.getId())
                .orElseGet(() -> {
                    Invoice inv = new Invoice();
                    inv.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    inv.setBooking(booking);
                    inv.setCompany(booking.getCompany());
                    inv.setPlantCharges(booking.getPlantCharges());
                    inv.setInstallationCharges(booking.getInstallationCharges());
                    inv.setMaintenanceCharges(booking.getMaintenanceCharges());
                    inv.setTaxAmount(booking.getTaxAmount());
                    inv.setDiscountAmount(booking.getDiscountAmount());
                    inv.setTotalAmount(booking.getGrandTotal());
                    inv.setPaymentStatus("PAID");
                    inv.setInvoiceDate(LocalDateTime.now());
                    return invoiceRepository.save(inv);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto.Response getPaymentById(Long id, String userEmail) {
        User user = getUser(userEmail);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        validateBookingAccess(payment.getBooking(), user);
        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto.Response> getPaymentsForBooking(Long bookingId, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        validateBookingAccess(booking, user);

        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto.InvoiceResponse getInvoiceByBookingId(Long bookingId, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        validateBookingAccess(booking, user);

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for booking: " + bookingId));
        return mapToInvoiceResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto.InvoiceResponse getInvoiceById(Long id, String userEmail) {
        User user = getUser(userEmail);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
        validateBookingAccess(invoice.getBooking(), user);
        return mapToInvoiceResponse(invoice);
    }

    private PaymentDto.Response mapToPaymentResponse(Payment payment) {
        PaymentDto.Response resp = new PaymentDto.Response();
        resp.setId(payment.getId());
        resp.setTransactionId(payment.getTransactionId());
        resp.setBookingId(payment.getBooking().getId());
        resp.setBookingNumber(payment.getBooking().getBookingNumber());
        resp.setAmount(payment.getAmount());
        resp.setCurrency("INR");
        resp.setPaymentMethod(payment.getPaymentMethod());
        resp.setPaymentStatus(payment.getStatus());
        resp.setGatewayReference(payment.getGatewayReference());
        resp.setPaymentDate(payment.getCreatedAt());
        return resp;
    }

    private PaymentDto.InvoiceResponse mapToInvoiceResponse(Invoice inv) {
        PaymentDto.InvoiceResponse resp = new PaymentDto.InvoiceResponse();
        resp.setId(inv.getId());
        resp.setInvoiceNumber(inv.getInvoiceNumber());
        resp.setBookingId(inv.getBooking().getId());
        resp.setBookingNumber(inv.getBooking().getBookingNumber());
        resp.setCompanyId(inv.getCompany().getId());
        resp.setCompanyName(inv.getCompany().getName());
        resp.setPlantCharges(inv.getPlantCharges());
        resp.setInstallationCharges(inv.getInstallationCharges());
        resp.setMaintenanceCharges(inv.getMaintenanceCharges());
        resp.setTaxAmount(inv.getTaxAmount());
        resp.setDiscountAmount(inv.getDiscountAmount());
        resp.setTotalAmount(inv.getTotalAmount());
        resp.setInvoiceDate(inv.getInvoiceDate());
        resp.setPaymentStatus(inv.getPaymentStatus());
        return resp;
    }
}
