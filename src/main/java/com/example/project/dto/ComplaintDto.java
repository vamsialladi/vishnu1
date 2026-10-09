package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ComplaintDto {

    public static class CreateRequest {
        @NotNull(message = "Company ID is required")
        private Long companyId;

        @NotBlank(message = "Category is required")
        private String category;

        @NotBlank(message = "Description is required")
        private String description;

        private Long bookingId;
        private Long serviceVisitId;
        private String photoUrl;
        private String priority; // LOW, MEDIUM, HIGH, CRITICAL

        public CreateRequest() {}

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public Long getServiceVisitId() { return serviceVisitId; }
        public void setServiceVisitId(Long serviceVisitId) { this.serviceVisitId = serviceVisitId; }

        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }

    public static class ResolveRequest {
        private String status; // ASSIGNED, UNDER_INVESTIGATION, RESOLVED, REJECTED
        private String investigationNotes;
        private String resolutionNotes;

        public ResolveRequest() {}

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getInvestigationNotes() { return investigationNotes; }
        public void setInvestigationNotes(String investigationNotes) { this.investigationNotes = investigationNotes; }

        public String getResolutionNotes() { return resolutionNotes; }
        public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    }

    public static class Response {
        private Long id;
        private Long companyId;
        private String companyName;
        private String category;
        private String description;
        private Long bookingId;
        private Long serviceVisitId;
        private String photoUrl;
        private String priority;
        private String status;
        private Long assignedAdminId;
        private String assignedAdminName;
        private String investigationNotes;
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

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public Long getServiceVisitId() { return serviceVisitId; }
        public void setServiceVisitId(Long serviceVisitId) { this.serviceVisitId = serviceVisitId; }

        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Long getAssignedAdminId() { return assignedAdminId; }
        public void setAssignedAdminId(Long assignedAdminId) { this.assignedAdminId = assignedAdminId; }

        public String getAssignedAdminName() { return assignedAdminName; }
        public void setAssignedAdminName(String assignedAdminName) { this.assignedAdminName = assignedAdminName; }

        public String getInvestigationNotes() { return investigationNotes; }
        public void setInvestigationNotes(String investigationNotes) { this.investigationNotes = investigationNotes; }

        public String getResolutionNotes() { return resolutionNotes; }
        public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getResolvedAt() { return resolvedAt; }
        public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    }
}
