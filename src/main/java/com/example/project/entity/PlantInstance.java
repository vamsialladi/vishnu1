package com.example.project.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "plant_instances")
public class PlantInstance {

    public enum HealthStatus {
        HEALTHY, NEEDS_ATTENTION, UNHEALTHY, CRITICAL, REPLACED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant_identifier", nullable = false, unique = true, length = 100)
    private String plantIdentifier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_location_id")
    private CompanyLocation companyLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "qr_code", nullable = false, unique = true, length = 255)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", nullable = false, length = 50)
    private HealthStatus healthStatus = HealthStatus.HEALTHY;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "custom_nickname", length = 100)
    private String customNickname;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "last_service_date")
    private LocalDateTime lastServiceDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public PlantInstance() {}

    public PlantInstance(String plantIdentifier, String qrCode, Product product, Company company,
                         CompanyLocation companyLocation, Booking booking, LocalDate installationDate) {
        this.plantIdentifier = plantIdentifier;
        this.qrCode = qrCode;
        this.product = product;
        this.company = company;
        this.companyLocation = companyLocation;
        this.booking = booking;
        this.installationDate = installationDate;
        this.healthStatus = HealthStatus.HEALTHY;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlantIdentifier() { return plantIdentifier; }
    public void setPlantIdentifier(String plantIdentifier) { this.plantIdentifier = plantIdentifier; }

    public String getUniquePlantCode() { return plantIdentifier; }
    public void setUniquePlantCode(String uniquePlantCode) { this.plantIdentifier = uniquePlantCode; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public CompanyLocation getCompanyLocation() { return companyLocation; }
    public void setCompanyLocation(CompanyLocation location) { this.companyLocation = location; }

    public CompanyLocation getLocation() { return companyLocation; }
    public void setLocation(CompanyLocation location) { this.companyLocation = location; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getQrCodeIdentifier() { return qrCode; }
    public void setQrCodeIdentifier(String qrCodeIdentifier) { this.qrCode = qrCodeIdentifier; }

    public HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }

    public String getCurrentHealthStatus() { return healthStatus != null ? healthStatus.name() : null; }
    public void setCurrentHealthStatus(String status) { this.healthStatus = status != null ? HealthStatus.valueOf(status) : null; }

    public LocalDate getInstallationDate() { return installationDate; }
    public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }

    public String getCustomNickname() { return customNickname; }
    public void setCustomNickname(String customNickname) { this.customNickname = customNickname; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDateTime lastServiceDate) { this.lastServiceDate = lastServiceDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
