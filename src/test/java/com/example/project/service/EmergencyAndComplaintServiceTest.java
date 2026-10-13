package com.example.project.service;

import com.example.project.dto.ComplaintDto;
import com.example.project.dto.EmergencyDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.serviceimpl.ComplaintServiceImpl;
import com.example.project.serviceimpl.EmergencyServiceImpl;
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
class EmergencyAndComplaintServiceTest {

    @Mock
    private EmergencyRequestRepository emergencyRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlantInstanceRepository instanceRepository;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ServiceVisitRepository visitRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EmergencyServiceImpl emergencyService;

    private User customer;
    private Company company;
    private EmergencyRequest emergencyRequest;

    @BeforeEach
    void setUp() {
        customer = new User("client", "client@example.com", "pass", "Client User", Role.ROLE_CUSTOMER);
        customer.setId(1L);

        company = new Company("Client Corp", "REG-99", "client@example.com", customer);
        company.setId(10L);

        emergencyRequest = new EmergencyRequest();
        emergencyRequest.setCompany(company);
        emergencyRequest.setProblemDescription("Severe plant wilting");
        emergencyRequest.setPriority(EmergencyRequest.Priority.HIGH);
        emergencyRequest.setPreferredVisitDate(java.time.LocalDateTime.now());
        emergencyRequest.setId(100L);
    }

    @Test
    @DisplayName("Should create emergency request successfully")
    void shouldCreateEmergencyRequest() {
        when(userRepository.findByEmail("client@example.com")).thenReturn(Optional.of(customer));
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(emergencyRepository.save(any(EmergencyRequest.class))).thenReturn(emergencyRequest);

        EmergencyDto.CreateRequest request = new EmergencyDto.CreateRequest();
        request.setCompanyId(10L);
        request.setProblemDescription("Severe plant wilting");
        request.setPriority("HIGH");
        request.setPreferredVisitDate(java.time.LocalDateTime.now());

        EmergencyDto.Response response = emergencyService.createEmergencyRequest(request, "client@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getPriority()).isEqualTo("HIGH");
        assertThat(response.getStatus()).isEqualTo("OPEN");
    }

    @Test
    @DisplayName("Should resolve complaint successfully")
    void shouldResolveComplaint() {
        ComplaintServiceImpl complaintService = new ComplaintServiceImpl(
                complaintRepository, companyRepository,
                bookingRepository, visitRepository, userRepository);

        User admin = new User("admin", "admin@example.com", "pass", "Admin", Role.ROLE_ADMIN);
        admin.setId(99L);

        Complaint complaint = new Complaint();
        complaint.setCompany(company);
        complaint.setCategory("SERVICE_QUALITY");
        complaint.setDescription("Technician was late");
        complaint.setPriority(Complaint.Priority.MEDIUM);
        complaint.setId(200L);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(complaintRepository.findById(200L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(complaint);

        ComplaintDto.ResolveRequest req = new ComplaintDto.ResolveRequest();
        req.setStatus("RESOLVED");
        req.setResolutionNotes("Apology issued and complementary maintenance visit provided.");

        ComplaintDto.Response resp = complaintService.resolveComplaint(200L, req, "admin@example.com");

        assertThat(resp).isNotNull();
        assertThat(complaint.getStatus()).isEqualTo(Complaint.Status.RESOLVED);
        assertThat(complaint.getResolutionNotes()).contains("Apology issued");
    }
}
