package com.example.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class InventoryDto {

    public static class StockUpdateRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity change is required")
        private Integer quantityChange; // can be positive or negative

        private String notes;

        public StockUpdateRequest() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantityChange() { return quantityChange; }
        public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class Response {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private int totalQuantity;
        private int availableQuantity;
        private int reservedQuantity;
        private int installedQuantity;
        private int rentalQuantity;
        private int damagedQuantity;
        private LocalDateTime updatedAt;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }

        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

        public int getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

        public int getReservedQuantity() { return reservedQuantity; }
        public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

        public int getInstalledQuantity() { return installedQuantity; }
        public void setInstalledQuantity(int installedQuantity) { this.installedQuantity = installedQuantity; }

        public int getRentalQuantity() { return rentalQuantity; }
        public void setRentalQuantity(int rentalQuantity) { this.rentalQuantity = rentalQuantity; }

        public int getDamagedQuantity() { return damagedQuantity; }
        public void setDamagedQuantity(int damagedQuantity) { this.damagedQuantity = damagedQuantity; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
