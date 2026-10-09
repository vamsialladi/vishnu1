package com.example.project.service;

import com.example.project.dto.MaintenanceDto;
import java.time.LocalDate;
import java.util.List;

public interface MaintenanceService {
    // Plans
    MaintenanceDto.PlanResponse createPlan(MaintenanceDto.PlanRequest request);
    List<MaintenanceDto.PlanResponse> getAllActivePlans();
    MaintenanceDto.PlanResponse getPlanById(Long id);
    MaintenanceDto.PlanResponse updatePlan(Long id, MaintenanceDto.PlanRequest request);
    void deletePlan(Long id);

    // Subscriptions
    List<MaintenanceDto.SubscriptionResponse> getSubscriptionsForCompany(Long companyId, String userEmail);
    MaintenanceDto.SubscriptionResponse getSubscriptionById(Long id, String userEmail);
    void pauseSubscription(Long id, String userEmail);
    void activateSubscription(Long id, String userEmail);
    void cancelSubscription(Long id, String userEmail);

    // Visits & Scheduler
    MaintenanceDto.ServiceVisitResponse scheduleVisit(Long subscriptionId, LocalDate visitDate, Long workerId);
    List<MaintenanceDto.ServiceVisitResponse> getVisitsForSubscription(Long subscriptionId, String userEmail);
    List<MaintenanceDto.ServiceVisitResponse> getVisitsForWorker(Long workerId);
    MaintenanceDto.ServiceVisitResponse recordArrival(Long visitId, String workerEmail);
    MaintenanceDto.ServiceVisitResponse completeVisit(Long visitId, String notes, String photosJson, String workerEmail);
    void generateUpcomingVisits();
}
