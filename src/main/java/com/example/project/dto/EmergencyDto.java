package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class EmergencyDto {

    public static class CreateRequest {
        @NotNull(message = "Company ID is required")
        private Long companyId;

        private Long plantInstanceId;

        @NotBlank(message = "Problem description is required")
        private String problemDescription;

        private String photoUrl;
        private LocalDateTime preferredVisitDate;
        private String priority; // LOW, MEDIUM, HIGH, CRITICAL

        public CreateRequest() {}

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public Long getPlantInstanceId() { return plantInstanceId; }
        public void setPlantInstanceId(Long plantInstanceId) { this.plantInstanceId = plantInstanceId; }

        public String getProblemDescription() { return problemDescription; }
        public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

        public LocalDateTime getPreferredVisitDate() { return preferredVisitDate; }
        public void setPreferredVisitDate(LocalDateTime preferredVisitDate) { this.preferredVisitDate = preferredVisitDate; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }

    public static class StatusUpdateRequest {
        private String status; // OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CANCELLED
        private Long assignedWorkerId;
        private String resolutionNotes;

        public StatusUpdateRequest() {}

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Long getAssignedWorkerId() { return assignedWorkerId; }
        public void setAssignedWorkerId(Long assignedWorkerId) { this.assignedWorkerId = assignedWorkerId; }

        public String getResolutionNotes() { return resolutionNotes; }
        public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    }

    public static class Response {
        private Long id;
        private Long companyId;
        private String companyName;
        private Long plantInstanceId;
        private String plantIdentifier;
        private String problemDescription;
        private String photoUrl;
        private LocalDateTime preferredVisitDate;
        private String priority;
        private String status;
        private Long assignedWorkerId;
        private String assignedWorkerName;
        private String resolutionNotes;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public Long getPlantInstanceId() { return plantInstanceId; }
        public void setPlantInstanceId(Long plantInstanceId) { this.plantInstanceId = plantInstanceId; }

        public String getPlantIdentifier() { return plantIdentifier; }
        public void setPlantIdentifier(String plantIdentifier) { this.plantIdentifier = plantIdentifier; }

        public String getProblemDescription() { return problemDescription; }
        public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

        public LocalDateTime getPreferredVisitDate() { return preferredVisitDate; }
        public void setPreferredVisitDate(LocalDateTime preferredVisitDate) { this.preferredVisitDate = preferredVisitDate; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Long getAssignedWorkerId() { return assignedWorkerId; }
        public void setAssignedWorkerId(Long assignedWorkerId) { this.assignedWorkerId = assignedWorkerId; }

        public String getAssignedWorkerName() { return assignedWorkerName; }
        public void setAssignedWorkerName(String assignedWorkerName) { this.assignedWorkerName = assignedWorkerName; }

        public String getResolutionNotes() { return resolutionNotes; }
        public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getResolvedAt() { return resolvedAt; }
        public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    }
}
