package com.example.project.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SiteInspectionDto {

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

    public static class AssessmentRequest {
        private String spaceAssessment;
        private String sunlightAssessment;
        private String temperatureAssessment;
        private String humidityAssessment;
        private Boolean indoorFeasible;
        private String plantRecommendations;
        private String inspectionNotes;

        public AssessmentRequest() {}

        public String getSpaceAssessment() { return spaceAssessment; }
        public void setSpaceAssessment(String spaceAssessment) { this.spaceAssessment = spaceAssessment; }

        public String getSunlightAssessment() { return sunlightAssessment; }
        public void setSunlightAssessment(String sunlightAssessment) { this.sunlightAssessment = sunlightAssessment; }

        public String getTemperatureAssessment() { return temperatureAssessment; }
        public void setTemperatureAssessment(String temperatureAssessment) { this.temperatureAssessment = temperatureAssessment; }

        public String getHumidityAssessment() { return humidityAssessment; }
        public void setHumidityAssessment(String humidityAssessment) { this.humidityAssessment = humidityAssessment; }

        public Boolean getIndoorFeasible() { return indoorFeasible; }
        public void setIndoorFeasible(Boolean indoorFeasible) { this.indoorFeasible = indoorFeasible; }

        public String getPlantRecommendations() { return plantRecommendations; }
        public void setPlantRecommendations(String plantRecommendations) { this.plantRecommendations = plantRecommendations; }

        public String getInspectionNotes() { return inspectionNotes; }
        public void setInspectionNotes(String inspectionNotes) { this.inspectionNotes = inspectionNotes; }
    }

    public static class Response {
        private Long id;
        private Long bookingId;
        private String bookingNumber;
        private Long assignedWorkerId;
        private String assignedWorkerName;
        private LocalDateTime scheduledDate;
        private LocalDateTime completedDate;
        private String status;
        private String spaceAssessment;
        private String sunlightAssessment;
        private String temperatureAssessment;
        private String humidityAssessment;
        private boolean indoorFeasible;
        private String plantRecommendations;
        private String inspectionNotes;

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

        public LocalDateTime getCompletedDate() { return completedDate; }
        public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getSpaceAssessment() { return spaceAssessment; }
        public void setSpaceAssessment(String spaceAssessment) { this.spaceAssessment = spaceAssessment; }

        public String getSunlightAssessment() { return sunlightAssessment; }
        public void setSunlightAssessment(String sunlightAssessment) { this.sunlightAssessment = sunlightAssessment; }

        public String getTemperatureAssessment() { return temperatureAssessment; }
        public void setTemperatureAssessment(String temperatureAssessment) { this.temperatureAssessment = temperatureAssessment; }

        public String getHumidityAssessment() { return humidityAssessment; }
        public void setHumidityAssessment(String humidityAssessment) { this.humidityAssessment = humidityAssessment; }

        public boolean isIndoorFeasible() { return indoorFeasible; }
        public void setIndoorFeasible(boolean indoorFeasible) { this.indoorFeasible = indoorFeasible; }

        public String getPlantRecommendations() { return plantRecommendations; }
        public void setPlantRecommendations(String plantRecommendations) { this.plantRecommendations = plantRecommendations; }

        public String getInspectionNotes() { return inspectionNotes; }
        public void setInspectionNotes(String inspectionNotes) { this.inspectionNotes = inspectionNotes; }
    }
}
