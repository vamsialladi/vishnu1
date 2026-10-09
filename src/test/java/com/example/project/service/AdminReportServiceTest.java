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
        when(userRepository.count()).thenReturn(15L);
        when(workerRepository.count()).thenReturn(5L);
        when(productRepository.count()).thenReturn(30L);
        when(bookingRepository.count()).thenReturn(20L);
        when(subscriptionRepository.findByStatus(Subscription.SubscriptionStatus.ACTIVE)).thenReturn(List.of(new Subscription()));
        when(siteInspectionRepository.findByStatus(SiteInspection.InspectionStatus.REQUESTED)).thenReturn(List.of());
        when(installationRepository.findByStatus(Installation.InstallationStatus.SCHEDULED)).thenReturn(List.of());
        when(emergencyRepository.findByStatus(EmergencyRequest.Status.OPEN)).thenReturn(List.of(new EmergencyRequest(), new EmergencyRequest()));
        when(complaintRepository.findByStatus(Complaint.Status.OPEN)).thenReturn(List.of(new Complaint()));

        Payment p1 = new Payment();
        p1.setAmount(BigDecimal.valueOf(1500));
        Payment p2 = new Payment();
        p2.setAmount(BigDecimal.valueOf(3500));
        when(paymentRepository.findByStatus("SUCCESS")).thenReturn(List.of(p1, p2));
        when(bookingRepository.findAll()).thenReturn(List.of());
        when(plantInstanceRepository.findAll()).thenReturn(List.of());

        AdminReportDto.DashboardStats stats = adminReportService.getDashboardStatistics();

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalCustomers()).isEqualTo(15L);
        assertThat(stats.getTotalWorkers()).isEqualTo(5L);
        assertThat(stats.getTotalPlants()).isEqualTo(30L);
        assertThat(stats.getTotalBookings()).isEqualTo(20L);
        assertThat(stats.getActiveSubscriptions()).isEqualTo(1L);
        assertThat(stats.getOpenEmergencies()).isEqualTo(2L);
        assertThat(stats.getOpenComplaints()).isEqualTo(1L);
        assertThat(stats.getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }
}
