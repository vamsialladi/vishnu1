package com.example.project.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class InstallationDto {

    public static class ScheduleRequest {
        @NotNull(message = "Booking ID is required")
        private Long bookingId;

        @NotNull(message = "Scheduled date/time is required")
        private LocalDateTime scheduledDate;

        private Long assignedWorkerId;

        public ScheduleRequest() {}

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

        public Long getAssignedWorkerId() { return assignedWorkerId; }
        public void setAssignedWorkerId(Long assignedWorkerId) { this.assignedWorkerId = assignedWorkerId; }
    }

    public static class CompleteRequest {
        private String beforePhotosJson;
        private String afterPhotosJson;
        private String installationNotes;

        public CompleteRequest() {}

        public String getBeforePhotosJson() { return beforePhotosJson; }
        public void setBeforePhotosJson(String beforePhotosJson) { this.beforePhotosJson = beforePhotosJson; }

        public String getAfterPhotosJson() { return afterPhotosJson; }
        public void setAfterPhotosJson(String afterPhotosJson) { this.afterPhotosJson = afterPhotosJson; }

        public String getInstallationNotes() { return installationNotes; }
        public void setInstallationNotes(String installationNotes) { this.installationNotes = installationNotes; }
    }

    public static class Response {
        private Long id;
        private Long bookingId;
        private String bookingNumber;
        private Long assignedWorkerId;
        private String assignedWorkerName;
        private LocalDateTime scheduledDate;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String status;
        private String beforePhotosJson;
        private String afterPhotosJson;
        private String installationNotes;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getBookingNumber() { return bookingNumber; }
        public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

        public Long getAssignedWorkerId() { return assignedWorkerId; }
        public void setAssignedWorkerId(Long assignedWorkerId) { this.assignedWorkerId = assignedWorkerId; }

        public String getAssignedWorkerName() { return assignedWorkerName; }
        public void setAssignedWorkerName(String assignedWorkerName) { this.assignedWorkerName = assignedWorkerName; }

        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getBeforePhotosJson() { return beforePhotosJson; }
        public void setBeforePhotosJson(String beforePhotosJson) { this.beforePhotosJson = beforePhotosJson; }

        public String getAfterPhotosJson() { return afterPhotosJson; }
        public void setAfterPhotosJson(String afterPhotosJson) { this.afterPhotosJson = afterPhotosJson; }

        public String getInstallationNotes() { return installationNotes; }
        public void setInstallationNotes(String installationNotes) { this.installationNotes = installationNotes; }
    }
}
