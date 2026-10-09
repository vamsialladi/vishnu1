package com.example.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    public static class AddItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity = 1;

        private boolean isRental = false;
        private boolean includeInstallation = true;
        private Long maintenancePlanId;

        public AddItemRequest() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public boolean isRental() { return isRental; }
        public void setRental(boolean rental) { isRental = rental; }

        public boolean isIncludeInstallation() { return includeInstallation; }
        public void setIncludeInstallation(boolean includeInstallation) { this.includeInstallation = includeInstallation; }

        public Long getMaintenancePlanId() { return maintenancePlanId; }
        public void setMaintenancePlanId(Long maintenancePlanId) { this.maintenancePlanId = maintenancePlanId; }
    }

    public static class UpdateItemRequest {
        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        public UpdateItemRequest() {}

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class ItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private int quantity;
        private boolean isRental;
        private BigDecimal unitPrice;
        private BigDecimal installationCharge;
        private BigDecimal maintenanceCharge;
        private BigDecimal subtotal;

        public ItemResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }

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

        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    public static class Response {
        private Long id;
        private Long userId;
        private List<ItemResponse> items;
        private BigDecimal subtotal;
        private BigDecimal totalInstallationCharges;
        private BigDecimal totalMaintenanceCharges;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal grandTotal;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public List<ItemResponse> getItems() { return items; }
        public void setItems(List<ItemResponse> items) { this.items = items; }

        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

        public BigDecimal getTotalInstallationCharges() { return totalInstallationCharges; }
        public void setTotalInstallationCharges(BigDecimal totalInstallationCharges) { this.totalInstallationCharges = totalInstallationCharges; }

        public BigDecimal getTotalMaintenanceCharges() { return totalMaintenanceCharges; }
        public void setTotalMaintenanceCharges(BigDecimal totalMaintenanceCharges) { this.totalMaintenanceCharges = totalMaintenanceCharges; }

        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

        public BigDecimal getGrandTotal() { return grandTotal; }
        public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

        // Compatibility getters for older test expectations
        public BigDecimal getInstallationCharge() { return totalInstallationCharges; }
        public BigDecimal getTax() { return taxAmount; }
    }
}
