package com.example.project.service;

import com.example.project.dto.SiteInspectionDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.serviceimpl.SiteInspectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SiteInspectionServiceTest {

    @Mock
    private SiteInspectionRepository inspectionRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CompanyLocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SiteInspectionServiceImpl siteInspectionService;

    private User inspector;
    private Booking booking;
    private SiteInspection inspection;

    @BeforeEach
    void setUp() {
        inspector = new User("inspector1", "inspector@example.com", "pass", "Inspector Dan", Role.ROLE_WORKER);
        inspector.setId(3L);

        User customer = new User("cust", "cust@example.com", "pass", "Customer", Role.ROLE_CUSTOMER);
        customer.setId(1L);

        booking = new Booking("BK-INSP", customer, null, null, null);
        booking.setId(10L);
        booking.setStatus(Booking.BookingStatus.SITE_INSPECTION_REQUIRED);

        inspection = new SiteInspection(booking, null, LocalDate.now().plusDays(2));
        inspection.setId(100L);
        inspection.setInspector(inspector);
        inspection.setStatus(SiteInspection.InspectionStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Should schedule site inspection successfully")
    void shouldScheduleInspection() {
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(3L)).thenReturn(Optional.of(inspector));
        when(inspectionRepository.save(any(SiteInspection.class))).thenReturn(inspection);

        SiteInspectionDto.ScheduleRequest request = new SiteInspectionDto.ScheduleRequest();
        request.setBookingId(10L);
        request.setAssignedWorkerId(3L);
        request.setScheduledDate(java.time.LocalDateTime.now().plusDays(2));

        SiteInspectionDto.Response response = siteInspectionService.scheduleInspection(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStatus()).isEqualTo("SCHEDULED");
    }

    @Test
    @DisplayName("Should record site assessment and update status to COMPLETED")
    void shouldRecordAssessment() {
        when(userRepository.findByEmail("inspector@example.com")).thenReturn(Optional.of(inspector));
        when(inspectionRepository.findById(100L)).thenReturn(Optional.of(inspection));
        when(inspectionRepository.save(any(SiteInspection.class))).thenReturn(inspection);

        SiteInspectionDto.AssessmentRequest request = new SiteInspectionDto.AssessmentRequest();
        request.setSpaceAssessment("120.5 sq ft");
        request.setSunlightAssessment("MODERATE");
        request.setTemperatureAssessment("24.0 C");
        request.setHumidityAssessment("55%");
        request.setIndoorFeasible(true);
        request.setPlantRecommendations("Snake plant, Peace lily recommended");
        request.setInspectionNotes("Well ventilated area");

        SiteInspectionDto.Response response = siteInspectionService.recordAssessment(100L, request, "inspector@example.com");

        assertThat(response).isNotNull();
        assertThat(inspection.getStatus()).isEqualTo(SiteInspection.InspectionStatus.COMPLETED);
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.SITE_INSPECTION_COMPLETED);
    }
}
