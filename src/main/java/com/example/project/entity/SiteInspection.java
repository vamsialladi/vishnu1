package com.example.project.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "site_inspections")
public class SiteInspection {

    public enum InspectionStatus {
        REQUESTED, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private CompanyLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private User inspector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker assignedWorker;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InspectionStatus status = InspectionStatus.REQUESTED;

    @Column(name = "space_assessment", columnDefinition = "TEXT")
    private String spaceAssessment;

    @Column(name = "sunlight_assessment", columnDefinition = "TEXT")
    private String sunlightAssessment;

    @Column(name = "temperature_assessment", length = 100)
    private String temperatureAssessment;

    @Column(name = "humidity_assessment", length = 100)
    private String humidityAssessment;

    @Column(name = "indoor_feasible")
    private boolean indoorFeasible = true;

    @Column(name = "recommended_plants", columnDefinition = "TEXT")
    private String recommendedPlants;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public SiteInspection() {}

    public SiteInspection(Booking booking, CompanyLocation location, LocalDate scheduledDate) {
        this.booking = booking;
        this.location = location;
        if (booking != null) {
            this.company = booking.getCompany();
        }
        if (scheduledDate != null) {
            this.scheduledDate = scheduledDate.atTime(10, 0);
        }
        this.status = InspectionStatus.SCHEDULED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public CompanyLocation getLocation() { return location; }
    public void setLocation(CompanyLocation location) { this.location = location; }

    public User getInspector() { return inspector; }
    public void setInspector(User inspector) { this.inspector = inspector; }

    public Worker getAssignedWorker() { return assignedWorker; }
    public void setAssignedWorker(Worker assignedWorker) { this.assignedWorker = assignedWorker; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public InspectionStatus getStatus() { return status; }
    public void setStatus(InspectionStatus status) { this.status = status; }

    public String getSpaceAssessment() { return spaceAssessment; }
    public void setSpaceAssessment(String spaceAssessment) { this.spaceAssessment = spaceAssessment; }

    public String getSunlightAssessment() { return sunlightAssessment; }
    public void setSunlightAssessment(String sunlightAssessment) { this.sunlightAssessment = sunlightAssessment; }

    public String getTemperatureAssessment() { return temperatureAssessment; }
    public void setTemperatureAssessment(String temperatureAssessment) { this.temperatureAssessment = temperatureAssessment; }

    public String getHumidityAssessment() { return humidityAssessment; }
    public void setHumidityAssessment(String humidityAssessment) { this.humidityAssessment = humidityAssessment; }

    public boolean isIndoorFeasible() { return indoorFeasible; }
    public void setIndoorFeasible(boolean indoorFeasible) { this.indoorFeasible = indoorFeasible; }

    public String getRecommendedPlants() { return recommendedPlants; }
    public void setRecommendedPlants(String recommendedPlants) { this.recommendedPlants = recommendedPlants; }

    public String getPlantRecommendations() { return recommendedPlants; }
    public void setPlantRecommendations(String plantRecommendations) { this.recommendedPlants = plantRecommendations; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getInspectionNotes() { return notes; }
    public void setInspectionNotes(String inspectionNotes) { this.notes = inspectionNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
