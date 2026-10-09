package com.example.project.service;

import com.example.project.dto.AdminReportDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.serviceimpl.AdminReportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SiteInspectionRepository siteInspectionRepository;

    @Mock
    private InstallationRepository installationRepository;

    @Mock
    private EmergencyRequestRepository emergencyRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PlantInstanceRepository plantInstanceRepository;

    @InjectMocks
    private AdminReportServiceImpl adminReportService;

    @Test
    @DisplayName("Should aggregate real dashboard statistics from database repositories")
    void shouldAggregateDashboardStatistics() {
        when(userRepository.countByRole(Role.ROLE_CUSTOMER)).thenReturn(15L);
        when(workerRepository.count()).thenReturn(5L);
        when(productRepository.count()).thenReturn(30L);
        when(plantInstanceRepository.count()).thenReturn(50L);
        when(bookingRepository.count()).thenReturn(20L);
        when(subscriptionRepository.countByStatus(Subscription.SubscriptionStatus.ACTIVE)).thenReturn(12L);
        when(emergencyRepository.countByStatus(EmergencyRequest.EmergencyStatus.OPEN)).thenReturn(2L);
        when(complaintRepository.countByStatus(Complaint.ComplaintStatus.OPEN)).thenReturn(1L);

        Payment p1 = new Payment(null, BigDecimal.valueOf(1500), Payment.PaymentStatus.SUCCESS, "TXN-1", "GATEWAY");
        Payment p2 = new Payment(null, BigDecimal.valueOf(3500), Payment.PaymentStatus.SUCCESS, "TXN-2", "GATEWAY");
        when(paymentRepository.findByStatus(Payment.PaymentStatus.SUCCESS)).thenReturn(List.of(p1, p2));

        AdminReportDto.DashboardStats stats = adminReportService.getDashboardStatistics();

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalCustomers()).isEqualTo(15L);
        assertThat(stats.getTotalWorkers()).isEqualTo(5L);
        assertThat(stats.getTotalPlantsInCatalog()).isEqualTo(30L);
        assertThat(stats.getTotalInstalledPlants()).isEqualTo(50L);
        assertThat(stats.getTotalBookings()).isEqualTo(20L);
        assertThat(stats.getActiveSubscriptions()).isEqualTo(12L);
        assertThat(stats.getOpenEmergencies()).isEqualTo(2L);
        assertThat(stats.getOpenComplaints()).isEqualTo(1L);
        assertThat(stats.getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }
}
