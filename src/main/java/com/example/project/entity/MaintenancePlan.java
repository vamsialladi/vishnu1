package com.example.project.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_plans")
public class MaintenancePlan {

    public enum Frequency {
        EVERY_4_DAYS, WEEKLY, TWICE_MONTHLY, MONTHLY, CUSTOM
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // BASIC, STANDARD, PREMIUM

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "monthly_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 50)
    private Frequency frequency = Frequency.WEEKLY;

    @Column(name = "included_services", length = 1000)
    private String includedServices;

    @Column(name = "replacement_support")
    private boolean replacementSupport = false;

    @Column(name = "emergency_support")
    private boolean emergencySupport = false;

    @Column(name = "consultation_support")
    private boolean consultationSupport = false;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public MaintenancePlan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }

    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }

    public String getIncludedServices() { return includedServices; }
    public void setIncludedServices(String includedServices) { this.includedServices = includedServices; }

    public boolean isReplacementSupport() { return replacementSupport; }
    public void setReplacementSupport(boolean replacementSupport) { this.replacementSupport = replacementSupport; }

    public boolean isEmergencySupport() { return emergencySupport; }
    public void setEmergencySupport(boolean emergencySupport) { this.emergencySupport = emergencySupport; }

    public boolean isConsultationSupport() { return consultationSupport; }
    public void setConsultationSupport(boolean consultationSupport) { this.consultationSupport = consultationSupport; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
