package com.example.project.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "plant_replacements")
public class PlantReplacement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_plant_instance_id", nullable = false)
    private PlantInstance oldPlantInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_plant_instance_id", nullable = false)
    private PlantInstance newPlantInstance;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "replacement_date", nullable = false)
    private LocalDate replacementDate = LocalDate.now();

    @Column(name = "replacement_cost", precision = 12, scale = 2)
    private BigDecimal replacementCost = BigDecimal.ZERO;

    @Column(name = "covered_by_plan")
    private boolean coveredByPlan = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_by_user_id")
    private User replacedByUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.replacementDate == null) {
            this.replacementDate = LocalDate.now();
        }
    }

    public PlantReplacement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlantInstance getOldPlantInstance() { return oldPlantInstance; }
    public void setOldPlantInstance(PlantInstance oldPlantInstance) { this.oldPlantInstance = oldPlantInstance; }

    public PlantInstance getNewPlantInstance() { return newPlantInstance; }
    public void setNewPlantInstance(PlantInstance newPlantInstance) { this.newPlantInstance = newPlantInstance; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getReplacementDate() { return replacementDate; }
    public void setReplacementDate(LocalDate replacementDate) { this.replacementDate = replacementDate; }

    public BigDecimal getReplacementCost() { return replacementCost; }
    public void setReplacementCost(BigDecimal replacementCost) { this.replacementCost = replacementCost; }

    public boolean isCoveredByPlan() { return coveredByPlan; }
    public void setCoveredByPlan(boolean coveredByPlan) { this.coveredByPlan = coveredByPlan; }

    public User getReplacedByUser() { return replacedByUser; }
    public void setReplacedByUser(User replacedByUser) { this.replacedByUser = replacedByUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
