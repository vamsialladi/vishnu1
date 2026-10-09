package com.example.project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plant_health_logs")
public class PlantHealthLog {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", nullable = false, length = 30)
    private PlantInstance.HealthStatus healthStatus = PlantInstance.HealthStatus.HEALTHY;

    @Column(name = "disease_name", length = 500)
    private String diseaseNotes;

    @Column(name = "pest_type", length = 500)
    private String pestNotes;

    @Column(name = "treatment_notes", columnDefinition = "TEXT")
    private String generalNotes;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.loggedAt == null) {
            this.loggedAt = LocalDateTime.now();
        }
    }

    public PlantHealthLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlantInstance getPlantInstance() { return plantInstance; }
    public void setPlantInstance(PlantInstance plantInstance) { this.plantInstance = plantInstance; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public User getRecordedByUser() { return recordedByUser; }
    public void setRecordedByUser(User recordedByUser) { this.recordedByUser = recordedByUser; }

    public PlantInstance.HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(PlantInstance.HealthStatus healthStatus) { this.healthStatus = healthStatus; }

    public String getDiseaseNotes() { return diseaseNotes; }
    public void setDiseaseNotes(String diseaseNotes) { this.diseaseNotes = diseaseNotes; }

    public String getPestNotes() { return pestNotes; }
    public void setPestNotes(String pestNotes) { this.pestNotes = pestNotes; }

    public String getGeneralNotes() { return generalNotes; }
    public void setGeneralNotes(String generalNotes) { this.generalNotes = generalNotes; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}
