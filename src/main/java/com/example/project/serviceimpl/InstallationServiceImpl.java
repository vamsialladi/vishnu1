package com.example.project.serviceimpl;

import com.example.project.dto.InstallationDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.InstallationService;
import com.example.project.service.InventoryService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstallationServiceImpl implements InstallationService {

    private final InstallationRepository installationRepository;
    private final BookingRepository bookingRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final PlantInstanceRepository plantInstanceRepository;
    private final InventoryService inventoryService;
    private final SubscriptionRepository subscriptionRepository;

    public InstallationServiceImpl(InstallationRepository installationRepository,
                                  BookingRepository bookingRepository,
                                  WorkerRepository workerRepository,
                                  UserRepository userRepository,
                                  PlantInstanceRepository plantInstanceRepository,
                                  InventoryService inventoryService,
                                  SubscriptionRepository subscriptionRepository) {
        this.installationRepository = installationRepository;
        this.bookingRepository = bookingRepository;
        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
        this.plantInstanceRepository = plantInstanceRepository;
        this.inventoryService = inventoryService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public InstallationDto.Response scheduleInstallation(InstallationDto.ScheduleRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        Installation installation = installationRepository.findByBookingId(booking.getId())
                .orElseGet(() -> {
                    Installation inst = new Installation();
                    inst.setBooking(booking);
                    return inst;
                });

        if (request.getAssignedWorkerId() != null) {
            Worker worker = workerRepository.findById(request.getAssignedWorkerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + request.getAssignedWorkerId()));
            installation.setAssignedWorker(worker);
        }

        installation.setScheduledDate(request.getScheduledDate());
        installation.setStatus(Installation.InstallationStatus.SCHEDULED);

        booking.setStatus(Booking.BookingStatus.INSTALLATION_SCHEDULED);
        booking.setInstallationDate(request.getScheduledDate().toLocalDate());
        bookingRepository.save(booking);

        Installation saved = installationRepository.save(installation);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InstallationDto.Response getInstallationByBookingId(Long bookingId) {
        Installation installation = installationRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Installation not found for booking id: " + bookingId));
        return mapToResponse(installation);
    }

    @Override
    @Transactional(readOnly = true)
    public InstallationDto.Response getInstallationById(Long id) {
        Installation installation = installationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installation not found with id: " + id));
        return mapToResponse(installation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstallationDto.Response> getInstallationsForWorker(Long workerId) {
        return installationRepository.findByAssignedWorkerId(workerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InstallationDto.Response startInstallation(Long installationId, String workerEmail) {
        User user = userRepository.findByEmail(workerEmail).or(() -> userRepository.findByUsername(workerEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + workerEmail));

        Installation installation = installationRepository.findById(installationId)
                .orElseThrow(() -> new ResourceNotFoundException("Installation not found with id: " + installationId));

        if (user.getRole() != Role.ROLE_ADMIN) {
            if (installation.getAssignedWorker() == null ||
                !installation.getAssignedWorker().getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access denied: You are not assigned to this installation.");
            }
        }

        installation.setStatus(Installation.InstallationStatus.IN_PROGRESS);
        installation.setStartedAt(LocalDateTime.now());

        Booking booking = installation.getBooking();
        booking.setStatus(Booking.BookingStatus.INSTALLATION_STARTED);
        bookingRepository.save(booking);

        Installation saved = installationRepository.save(installation);
        return mapToResponse(saved);
    }

    @Override
    public InstallationDto.Response completeInstallation(Long installationId, InstallationDto.CompleteRequest request, String workerEmail) {
        User user = userRepository.findByEmail(workerEmail).or(() -> userRepository.findByUsername(workerEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + workerEmail));

        Installation installation = installationRepository.findById(installationId)
                .orElseThrow(() -> new ResourceNotFoundException("Installation not found with id: " + installationId));

        if (user.getRole() != Role.ROLE_ADMIN) {
            if (installation.getAssignedWorker() == null ||
                !installation.getAssignedWorker().getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access denied: You are not assigned to this installation.");
            }
        }

        installation.setStatus(Installation.InstallationStatus.COMPLETED);
        installation.setCompletedAt(LocalDateTime.now());
        installation.setBeforePhotosJson(request.getBeforePhotosJson());
        installation.setAfterPhotosJson(request.getAfterPhotosJson());
        installation.setInstallationNotes(request.getInstallationNotes());

        Booking booking = installation.getBooking();
        booking.setStatus(Booking.BookingStatus.INSTALLATION_COMPLETED);

        // Register individual PlantInstances and deduct inventory from reserved to installed/rental
        for (BookingItem item : booking.getItems()) {
            inventoryService.confirmInstallation(item.getProduct().getId(), item.getQuantity(), item.isRental());

            for (int i = 0; i < item.getQuantity(); i++) {
                String plantCode = "PLANT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String qrCode = "QR-" + plantCode;

                PlantInstance instance = new PlantInstance();
                instance.setPlantIdentifier(plantCode);
                instance.setQrCode(qrCode);
                instance.setProduct(item.getProduct());
                instance.setCompany(booking.getCompany());
                instance.setCompanyLocation(booking.getCompanyLocation());
                instance.setBooking(booking);
                instance.setInstallationDate(LocalDate.now());
                instance.setHealthStatus(PlantInstance.HealthStatus.HEALTHY);

                plantInstanceRepository.save(instance);
            }
        }

        // If booking has a maintenance plan, activate maintenance subscription
        if (booking.getMaintenancePlan() != null) {
            booking.setStatus(Booking.BookingStatus.MAINTENANCE_ACTIVE);

            Subscription subscription = new Subscription();
            subscription.setCompany(booking.getCompany());
            subscription.setMaintenancePlan(booking.getMaintenancePlan());
            subscription.setBooking(booking);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusMonths(12));
            subscription.setNextServiceDate(LocalDate.now().plusDays(7));
            subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(subscription);
        }

        bookingRepository.save(booking);
        Installation saved = installationRepository.save(installation);
        return mapToResponse(saved);
    }

    private InstallationDto.Response mapToResponse(Installation installation) {
        InstallationDto.Response resp = new InstallationDto.Response();
        resp.setId(installation.getId());
        resp.setBookingId(installation.getBooking().getId());
        resp.setBookingNumber(installation.getBooking().getBookingNumber());
        if (installation.getAssignedWorker() != null) {
            resp.setAssignedWorkerId(installation.getAssignedWorker().getId());
            resp.setAssignedWorkerName(installation.getAssignedWorker().getUser().getFullName());
        }
        resp.setScheduledDate(installation.getScheduledDate());
        resp.setStartedAt(installation.getStartedAt());
        resp.setCompletedAt(installation.getCompletedAt());
        resp.setStatus(installation.getStatus().name());
        resp.setBeforePhotosJson(installation.getBeforePhotosJson());
        resp.setAfterPhotosJson(installation.getAfterPhotosJson());
        resp.setInstallationNotes(installation.getInstallationNotes());
        return resp;
    }
}
