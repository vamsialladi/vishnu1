package com.example.project.serviceimpl;

import com.example.project.dto.AdminReportDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.AdminReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AdminReportServiceImpl implements AdminReportService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final ProductRepository productRepository;
    private final BookingRepository bookingRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SiteInspectionRepository siteInspectionRepository;
    private final InstallationRepository installationRepository;
    private final EmergencyRequestRepository emergencyRepository;
    private final ComplaintRepository complaintRepository;
    private final PaymentRepository paymentRepository;
    private final PlantInstanceRepository plantInstanceRepository;

    public AdminReportServiceImpl(UserRepository userRepository,
                                  WorkerRepository workerRepository,
                                  ProductRepository productRepository,
                                  BookingRepository bookingRepository,
                                  SubscriptionRepository subscriptionRepository,
                                  SiteInspectionRepository siteInspectionRepository,
                                  InstallationRepository installationRepository,
                                  EmergencyRequestRepository emergencyRepository,
                                  ComplaintRepository complaintRepository,
                                  PaymentRepository paymentRepository,
                                  PlantInstanceRepository plantInstanceRepository) {
        this.userRepository = userRepository;
        this.workerRepository = workerRepository;
        this.productRepository = productRepository;
        this.bookingRepository = bookingRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.siteInspectionRepository = siteInspectionRepository;
        this.installationRepository = installationRepository;
        this.emergencyRepository = emergencyRepository;
        this.complaintRepository = complaintRepository;
        this.paymentRepository = paymentRepository;
        this.plantInstanceRepository = plantInstanceRepository;
    }

    @Override
    public AdminReportDto.DashboardStats getDashboardStatistics() {
        AdminReportDto.DashboardStats stats = new AdminReportDto.DashboardStats();

        stats.setTotalCustomers(userRepository.count());
        stats.setTotalWorkers(workerRepository.count());
        stats.setTotalPlants(productRepository.count());
        stats.setTotalBookings(bookingRepository.count());

        stats.setActiveSubscriptions(subscriptionRepository.findByStatus(Subscription.SubscriptionStatus.ACTIVE).size());
        stats.setPendingInspections(siteInspectionRepository.findByStatus(SiteInspection.InspectionStatus.REQUESTED).size());
        stats.setScheduledInstallations(installationRepository.findByStatus(Installation.InstallationStatus.SCHEDULED).size());
        stats.setOpenEmergencies(emergencyRepository.findByStatus(EmergencyRequest.Status.OPEN).size());
        stats.setOpenComplaints(complaintRepository.findByStatus(Complaint.Status.OPEN).size());

        // Calculate actual total revenue from successful payments
        List<Payment> successfulPayments = paymentRepository.findByStatus("SUCCESS");
        BigDecimal totalRev = successfulPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRev);

        // Bookings by status map
        Map<String, Long> bkgMap = new HashMap<>();
        for (Booking b : bookingRepository.findAll()) {
            bkgMap.put(b.getStatus().name(), bkgMap.getOrDefault(b.getStatus().name(), 0L) + 1);
        }
        stats.setBookingsByStatus(bkgMap);

        // Plant health distribution map
        Map<String, Long> healthMap = new HashMap<>();
        for (PlantInstance p : plantInstanceRepository.findAll()) {
            healthMap.put(p.getHealthStatus().name(), healthMap.getOrDefault(p.getHealthStatus().name(), 0L) + 1);
        }
        stats.setPlantHealthDistribution(healthMap);

        return stats;
    }
}
