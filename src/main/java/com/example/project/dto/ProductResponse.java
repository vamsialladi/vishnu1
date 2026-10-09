package com.example.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {

    private Long id;
    private String name;
    private String scientificName;
    private String description;
    private BigDecimal price;
    private BigDecimal rentalPrice;
    private BigDecimal installationCharge;
    private Integer quantity;
    private String category;
    private String sku;
    private String sunlightRequirement;
    private String waterRequirement;
    private String maintenanceLevel;
    private boolean indoor;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse() {}

    public ProductResponse(Long id, String name, String scientificName, String description, BigDecimal price, BigDecimal rentalPrice, BigDecimal installationCharge, Integer quantity, String category, String sku, String sunlightRequirement, String waterRequirement, String maintenanceLevel, boolean indoor, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.scientificName = scientificName;
        this.description = description;
        this.price = price;
        this.rentalPrice = rentalPrice;
        this.installationCharge = installationCharge;
        this.quantity = quantity;
        this.category = category;
        this.sku = sku;
        this.sunlightRequirement = sunlightRequirement;
        this.waterRequirement = waterRequirement;
        this.maintenanceLevel = maintenanceLevel;
        this.indoor = indoor;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(BigDecimal rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public BigDecimal getInstallationCharge() {
        return installationCharge;
    }

    public void setInstallationCharge(BigDecimal installationCharge) {
        this.installationCharge = installationCharge;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSunlightRequirement() {
        return sunlightRequirement;
    }

    public void setSunlightRequirement(String sunlightRequirement) {
        this.sunlightRequirement = sunlightRequirement;
    }

    public String getWaterRequirement() {
        return waterRequirement;
    }

    public void setWaterRequirement(String waterRequirement) {
        this.waterRequirement = waterRequirement;
    }

    public String getMaintenanceLevel() {
        return maintenanceLevel;
    }

    public void setMaintenanceLevel(String maintenanceLevel) {
        this.maintenanceLevel = maintenanceLevel;
    }

    public boolean isIndoor() {
        return indoor;
    }

    public void setIndoor(boolean indoor) {
        this.indoor = indoor;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
