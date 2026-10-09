package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaintenanceDto {

    public static class PlanRequest {
        @NotBlank(message = "Plan name is required")
        private String name;
        private String description;
        @NotNull(message = "Monthly price is required")
        private BigDecimal monthlyPrice;
        private String frequency; // EVERY_4_DAYS, WEEKLY, TWICE_MONTHLY, MONTHLY, CUSTOM
        private String includedServices;
        private boolean replacementSupport = false;
        private boolean emergencySupport = false;
        private boolean consultationSupport = false;

        public PlanRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getMonthlyPrice() { return monthlyPrice; }
        public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }

        public String getIncludedServices() { return includedServices; }
        public void setIncludedServices(String includedServices) { this.includedServices = includedServices; }

        public boolean isReplacementSupport() { return replacementSupport; }
        public void setReplacementSupport(boolean replacementSupport) { this.replacementSupport = replacementSupport; }

        public boolean isEmergencySupport() { return emergencySupport; }
        public void setEmergencySupport(boolean emergencySupport) { this.emergencySupport = emergencySupport; }

        public boolean isConsultationSupport() { return consultationSupport; }
        public void setConsultationSupport(boolean consultationSupport) { this.consultationSupport = consultationSupport; }
    }

    public static class PlanResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal monthlyPrice;
        private String frequency;
        private String includedServices;
        private boolean replacementSupport;
        private boolean emergencySupport;
        private boolean consultationSupport;
        private boolean isActive;

        public PlanResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getMonthlyPrice() { return monthlyPrice; }
        public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }

        public String getIncludedServices() { return includedServices; }
        public void setIncludedServices(String includedServices) { this.includedServices = includedServices; }

        public boolean isReplacementSupport() { return replacementSupport; }
        public void setReplacementSupport(boolean replacementSupport) { this.replacementSupport = replacementSupport; }

        public boolean isEmergencySupport() { return emergencySupport; }
        public void setEmergencySupport(boolean emergencySupport) { this.emergencySupport = emergencySupport; }

        public boolean isConsultationSupport() { return consultationSupport; }
        public void setConsultationSupport(boolean consultationSupport) { this.consultationSupport = consultationSupport; }

        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }

    public static class SubscriptionResponse {
        private Long id;
        private Long companyId;
        private String companyName;
        private Long maintenancePlanId;
        private String maintenancePlanName;
        private Long bookingId;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate nextServiceDate;
        private String status;
        private boolean autoRenew;

        public SubscriptionResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public Long getMaintenancePlanId() { return maintenancePlanId; }
        public void setMaintenancePlanId(Long maintenancePlanId) { this.maintenancePlanId = maintenancePlanId; }

        public String getMaintenancePlanName() { return maintenancePlanName; }
        public void setMaintenancePlanName(String maintenancePlanName) { this.maintenancePlanName = maintenancePlanName; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public LocalDate getNextServiceDate() { return nextServiceDate; }
        public void setNextServiceDate(LocalDate nextServiceDate) { this.nextServiceDate = nextServiceDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public boolean isAutoRenew() { return autoRenew; }
        public void setAutoRenew(boolean autoRenew) { this.autoRenew = autoRenew; }
    }

    public static class ServiceVisitResponse {
        private Long id;
        private Long subscriptionId;
        private Long companyId;
        private Long assignedWorkerId;
        private String assignedWorkerName;
        private LocalDate scheduledDate;
        private LocalDateTime arrivalTime;
        private LocalDateTime completedTime;
        private String status;
        private String serviceNotes;
        private String photosJson;

        public ServiceVisitResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getSubscriptionId() { return subscriptionId; }
        public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public Long getAssignedWorkerId() { return assignedWorkerId; }
        public void setAssignedWorkerId(Long assignedWorkerId) { this.assignedWorkerId = assignedWorkerId; }

        public String getAssignedWorkerName() { return assignedWorkerName; }
        public void setAssignedWorkerName(String assignedWorkerName) { this.assignedWorkerName = assignedWorkerName; }

        public LocalDate getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

        public LocalDateTime getArrivalTime() { return arrivalTime; }
        public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

        public LocalDateTime getCompletedTime() { return completedTime; }
        public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getServiceNotes() { return serviceNotes; }
        public void setServiceNotes(String serviceNotes) { this.serviceNotes = serviceNotes; }

        public String getPhotosJson() { return photosJson; }
        public void setPhotosJson(String photosJson) { this.photosJson = photosJson; }
    }
}
