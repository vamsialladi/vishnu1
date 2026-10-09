package com.example.project.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BookingDto {

    public static class CreateRequest {
        @NotNull(message = "Company ID is required")
        private Long companyId;

        private Long companyLocationId;
        private LocalDate installationDate;
        private boolean requireInspection = false;
        private String notes;

        public CreateRequest() {}

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public Long getCompanyLocationId() { return companyLocationId; }
        public void setCompanyLocationId(Long companyLocationId) { this.companyLocationId = companyLocationId; }

        public LocalDate getInstallationDate() { return installationDate; }
        public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }

        public boolean isRequireInspection() { return requireInspection; }
        public void setRequireInspection(boolean requireInspection) { this.requireInspection = requireInspection; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class StatusUpdateRequest {
        @NotNull(message = "Status is required")
        private String status;

        public StatusUpdateRequest() {}

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private int quantity;
        private boolean isRental;
        private BigDecimal unitPrice;
        private BigDecimal installationCharge;
        private BigDecimal maintenanceCharge;
        private BigDecimal totalPrice;

        public ItemResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public boolean isRental() { return isRental; }
        public void setRental(boolean rental) { isRental = rental; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getInstallationCharge() { return installationCharge; }
        public void setInstallationCharge(BigDecimal installationCharge) { this.installationCharge = installationCharge; }

        public BigDecimal getMaintenanceCharge() { return maintenanceCharge; }
        public void setMaintenanceCharge(BigDecimal maintenanceCharge) { this.maintenanceCharge = maintenanceCharge; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }

    public static class Response {
        private Long id;
        private String bookingNumber;
        private Long userId;
        private Long companyId;
        private String companyName;
        private Long companyLocationId;
        private String locationName;
        private String status;
        private String paymentStatus;
        private BigDecimal plantCharges;
        private BigDecimal installationCharges;
        private BigDecimal maintenanceCharges;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal grandTotal;
        private LocalDate installationDate;
        private Long maintenancePlanId;
        private String maintenancePlanName;
        private String notes;
        private LocalDateTime createdAt;
        private List<ItemResponse> items;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getBookingNumber() { return bookingNumber; }
        public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public Long getCompanyLocationId() { return companyLocationId; }
        public void setCompanyLocationId(Long companyLocationId) { this.companyLocationId = companyLocationId; }

        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

        public BigDecimal getPlantCharges() { return plantCharges; }
        public void setPlantCharges(BigDecimal plantCharges) { this.plantCharges = plantCharges; }

        public BigDecimal getInstallationCharges() { return installationCharges; }
        public void setInstallationCharges(BigDecimal installationCharges) { this.installationCharges = installationCharges; }

        public BigDecimal getMaintenanceCharges() { return maintenanceCharges; }
        public void setMaintenanceCharges(BigDecimal maintenanceCharges) { this.maintenanceCharges = maintenanceCharges; }

        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

        public BigDecimal getGrandTotal() { return grandTotal; }
        public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

        public LocalDate getInstallationDate() { return installationDate; }
        public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }

        public Long getMaintenancePlanId() { return maintenancePlanId; }
        public void setMaintenancePlanId(Long maintenancePlanId) { this.maintenancePlanId = maintenancePlanId; }

        public String getMaintenancePlanName() { return maintenancePlanName; }
        public void setMaintenancePlanName(String maintenancePlanName) { this.maintenancePlanName = maintenancePlanName; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public List<ItemResponse> getItems() { return items; }
        public void setItems(List<ItemResponse> items) { this.items = items; }
    }
}
