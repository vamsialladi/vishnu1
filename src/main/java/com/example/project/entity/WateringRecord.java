package com.example.project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "watering_records")
public class WateringRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_instance_id", nullable = false)
    private PlantInstance plantInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id")
    private User recordedByUser;

    @Column(name = "amount_liters")
    private Double amountLiters;

    @Column(name = "watered_at", nullable = false)
    private LocalDateTime wateredAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (this.wateredAt == null) {
            this.wateredAt = LocalDateTime.now();
        }
    }

    public WateringRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlantInstance getPlantInstance() { return plantInstance; }
    public void setPlantInstance(PlantInstance plantInstance) { this.plantInstance = plantInstance; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public User getRecordedByUser() { return recordedByUser; }
    public void setRecordedByUser(User recordedByUser) { this.recordedByUser = recordedByUser; }

    public Double getAmountLiters() { return amountLiters; }
    public void setAmountLiters(Double amountLiters) { this.amountLiters = amountLiters; }

    public LocalDateTime getWateredAt() { return wateredAt; }
    public void setWateredAt(LocalDateTime wateredAt) { this.wateredAt = wateredAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
