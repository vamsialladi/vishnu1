package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlantTrackingDto {

    public static class HealthLogRequest {
        @NotNull(message = "Health status is required")
        private String healthStatus; // HEALTHY, NEEDS_ATTENTION, UNHEALTHY, CRITICAL, REPLACED
        private String diseaseNotes;
        private String pestNotes;
        private String generalNotes;
        private String photoUrl;

        public HealthLogRequest() {}

        public String getHealthStatus() { return healthStatus; }
        public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

        public String getDiseaseNotes() { return diseaseNotes; }
        public void setDiseaseNotes(String diseaseNotes) { this.diseaseNotes = diseaseNotes; }

        public String getPestNotes() { return pestNotes; }
        public void setPestNotes(String pestNotes) { this.pestNotes = pestNotes; }

        public String getGeneralNotes() { return generalNotes; }
        public void setGeneralNotes(String generalNotes) { this.generalNotes = generalNotes; }

        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    }

    public static class WateringRequest {
        private Double amountLiters;
        private String notes;

        public WateringRequest() {}

        public Double getAmountLiters() { return amountLiters; }
        public void setAmountLiters(Double amountLiters) { this.amountLiters = amountLiters; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class FertilizerRequest {
        @NotBlank(message = "Fertilizer type is required")
        private String fertilizerType;
        private Double quantityGrams;
        private String notes;

        public FertilizerRequest() {}

        public String getFertilizerType() { return fertilizerType; }
        public void setFertilizerType(String fertilizerType) { this.fertilizerType = fertilizerType; }

        public Double getQuantityGrams() { return quantityGrams; }
        public void setQuantityGrams(Double quantityGrams) { this.quantityGrams = quantityGrams; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class ReplacementRequest {
        @NotNull(message = "New Product ID is required")
        private Long newProductId;
        private String reason;
        private BigDecimal replacementCost = BigDecimal.ZERO;
        private boolean coveredByPlan = true;

        public ReplacementRequest() {}

        public Long getNewProductId() { return newProductId; }
        public void setNewProductId(Long newProductId) { this.newProductId = newProductId; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public BigDecimal getReplacementCost() { return replacementCost; }
        public void setReplacementCost(BigDecimal replacementCost) { this.replacementCost = replacementCost; }

        public boolean isCoveredByPlan() { return coveredByPlan; }
        public void setCoveredByPlan(boolean coveredByPlan) { this.coveredByPlan = coveredByPlan; }
    }

    public static class InstanceResponse {
        private Long id;
        private String plantIdentifier;
        private String qrCode;
        private Long productId;
        private String productName;
        private Long companyId;
        private String companyName;
        private Long locationId;
        private String locationName;
        private Long bookingId;
        private LocalDate installationDate;
        private String healthStatus;
        private LocalDateTime lastServiceDate;
        private String notes;

        public InstanceResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getPlantIdentifier() { return plantIdentifier; }
        public void setPlantIdentifier(String plantIdentifier) { this.plantIdentifier = plantIdentifier; }

        public String getQrCode() { return qrCode; }
        public void setQrCode(String qrCode) { this.qrCode = qrCode; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public Long getLocationId() { return locationId; }
        public void setLocationId(Long locationId) { this.locationId = locationId; }

        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public LocalDate getInstallationDate() { return installationDate; }
        public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }

        public String getHealthStatus() { return healthStatus; }
        public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

        public LocalDateTime getLastServiceDate() { return lastServiceDate; }
        public void setLastServiceDate(LocalDateTime lastServiceDate) { this.lastServiceDate = lastServiceDate; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
