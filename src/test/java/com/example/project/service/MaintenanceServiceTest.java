package com.example.project.service;

import com.example.project.dto.MaintenanceDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.serviceimpl.MaintenanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceTest {

    @Mock
    private MaintenancePlanRepository planRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ServiceVisitRepository visitRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MaintenanceServiceImpl maintenanceService;

    private User owner;
    private Company company;
    private MaintenancePlan plan;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        owner = new User("owner", "owner@example.com", "pass", "Company Owner", Role.ROLE_CUSTOMER);
        owner.setId(1L);

        company = new Company("Green Office", "REG-555", "owner@example.com", owner);
        company.setId(10L);

        plan = new MaintenancePlan("Standard Plan", "Weekly care", BigDecimal.valueOf(1500),
                MaintenancePlan.MaintenanceFrequency.WEEKLY, "Watering, pruning");
        plan.setId(20L);

        subscription = new Subscription(null, company, null, owner, plan, LocalDate.now());
        subscription.setId(30L);
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should create maintenance plan successfully")
    void shouldCreateMaintenancePlan() {
        when(planRepository.save(any(MaintenancePlan.class))).thenReturn(plan);

        MaintenanceDto.PlanRequest request = new MaintenanceDto.PlanRequest();
        request.setName("Standard Plan");
        request.setDescription("Weekly care");
        request.setMonthlyPrice(BigDecimal.valueOf(1500));
        request.setFrequency("WEEKLY");

        MaintenanceDto.PlanResponse response = maintenanceService.createPlan(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Standard Plan");
    }

    @Test
    @DisplayName("Should pause and activate subscription")
    void shouldPauseAndActivateSubscription() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(subscriptionRepository.findById(30L)).thenReturn(Optional.of(subscription));

        maintenanceService.pauseSubscription(30L, "owner@example.com");
        assertThat(subscription.getStatus()).isEqualTo(Subscription.SubscriptionStatus.PAUSED);

        maintenanceService.activateSubscription(30L, "owner@example.com");
        assertThat(subscription.getStatus()).isEqualTo(Subscription.SubscriptionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should record worker arrival on service visit")
    void shouldRecordWorkerArrival() {
        User workerUser = new User("worker", "worker@example.com", "pass", "Worker John", Role.ROLE_WORKER);
        workerUser.setId(2L);

        Worker worker = new Worker(workerUser, "EMP-001", "Pruning Specialist");
        worker.setId(5L);

        ServiceVisit visit = new ServiceVisit(subscription, LocalDate.now());
        visit.setId(100L);
        visit.setAssignedWorker(worker);

        when(userRepository.findByEmail("worker@example.com")).thenReturn(Optional.of(workerUser));
        when(workerRepository.findByUserId(2L)).thenReturn(Optional.of(worker));
        when(visitRepository.findById(100L)).thenReturn(Optional.of(visit));

        MaintenanceDto.ServiceVisitResponse response = maintenanceService.recordArrival(100L, "worker@example.com");

        assertThat(response).isNotNull();
        assertThat(visit.getArrivalTime()).isNotNull();
        assertThat(visit.getStatus()).isEqualTo(ServiceVisit.VisitStatus.IN_PROGRESS);
    }
}
