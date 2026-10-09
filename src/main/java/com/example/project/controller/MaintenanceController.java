package com.example.project.controller;

import com.example.project.dto.MaintenanceDto;
import com.example.project.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // Plans
    @PostMapping("/plans")
    public ResponseEntity<MaintenanceDto.PlanResponse> createPlan(@Valid @RequestBody MaintenanceDto.PlanRequest request) {
        return new ResponseEntity<>(maintenanceService.createPlan(request), HttpStatus.CREATED);
    }

    @GetMapping("/plans")
    public ResponseEntity<List<MaintenanceDto.PlanResponse>> getAllPlans() {
        return ResponseEntity.ok(maintenanceService.getAllActivePlans());
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<MaintenanceDto.PlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.getPlanById(id));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<MaintenanceDto.PlanResponse> updatePlan(@PathVariable Long id,
                                                                 @Valid @RequestBody MaintenanceDto.PlanRequest request) {
        return ResponseEntity.ok(maintenanceService.updatePlan(id, request));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        maintenanceService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    // Subscriptions
    @GetMapping("/subscriptions/company/{companyId}")
    public ResponseEntity<List<MaintenanceDto.SubscriptionResponse>> getSubscriptions(@PathVariable Long companyId,
                                                                                      @RequestParam String username) {
        return ResponseEntity.ok(maintenanceService.getSubscriptionsForCompany(companyId, username));
    }

    @GetMapping("/subscriptions/{id}")
    public ResponseEntity<MaintenanceDto.SubscriptionResponse> getSubscriptionById(@PathVariable Long id,
                                                                                   @RequestParam String username) {
        return ResponseEntity.ok(maintenanceService.getSubscriptionById(id, username));
    }

    @PostMapping("/subscriptions/{id}/pause")
    public ResponseEntity<Void> pauseSubscription(@PathVariable Long id, @RequestParam String username) {
        maintenanceService.pauseSubscription(id, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscriptions/{id}/activate")
    public ResponseEntity<Void> activateSubscription(@PathVariable Long id, @RequestParam String username) {
        maintenanceService.activateSubscription(id, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscriptions/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long id, @RequestParam String username) {
        maintenanceService.cancelSubscription(id, username);
        return ResponseEntity.ok().build();
    }

    // Visits
    @PostMapping("/visits/schedule")
    public ResponseEntity<MaintenanceDto.ServiceVisitResponse> scheduleVisit(
            @RequestParam Long subscriptionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate,
            @RequestParam(required = false) Long workerId) {
        return new ResponseEntity<>(maintenanceService.scheduleVisit(subscriptionId, visitDate, workerId), HttpStatus.CREATED);
    }

    @GetMapping("/visits/subscription/{subscriptionId}")
    public ResponseEntity<List<MaintenanceDto.ServiceVisitResponse>> getVisitsForSubscription(@PathVariable Long subscriptionId,
                                                                                              @RequestParam String username) {
        return ResponseEntity.ok(maintenanceService.getVisitsForSubscription(subscriptionId, username));
    }

    @GetMapping("/visits/worker/{workerId}")
    public ResponseEntity<List<MaintenanceDto.ServiceVisitResponse>> getVisitsForWorker(@PathVariable Long workerId) {
        return ResponseEntity.ok(maintenanceService.getVisitsForWorker(workerId));
    }

    @PostMapping("/visits/{id}/arrival")
    public ResponseEntity<MaintenanceDto.ServiceVisitResponse> recordArrival(@PathVariable Long id,
                                                                             @RequestParam String username) {
        return ResponseEntity.ok(maintenanceService.recordArrival(id, username));
    }

    @PostMapping("/visits/{id}/complete")
    public ResponseEntity<MaintenanceDto.ServiceVisitResponse> completeVisit(
            @PathVariable Long id,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) String photosJson,
            @RequestParam String username) {
        return ResponseEntity.ok(maintenanceService.completeVisit(id, notes, photosJson, username));
    }
}
