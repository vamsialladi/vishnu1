package com.example.project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "green_office_scores")
public class GreenOfficeScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private int score; // 0 - 100

    private int totalPlants;
    private int healthyPlants;
    private double greenCoverageSqFt;
    private int speciesDiversityCount;
    private double maintenanceComplianceRate; // Percentage (e.g., 95.5)

    @Column(length = 2000)
    private String breakdownJson;

    @Column(length = 2000)
    private String recommendations;

    @Column(nullable = false)
    private LocalDateTime calculatedAt = LocalDateTime.now();

    public GreenOfficeScore() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalPlants() { return totalPlants; }
    public void setTotalPlants(int totalPlants) { this.totalPlants = totalPlants; }

    public int getHealthyPlants() { return healthyPlants; }
    public void setHealthyPlants(int healthyPlants) { this.healthyPlants = healthyPlants; }

    public double getGreenCoverageSqFt() { return greenCoverageSqFt; }
    public void setGreenCoverageSqFt(double greenCoverageSqFt) { this.greenCoverageSqFt = greenCoverageSqFt; }

    public int getSpeciesDiversityCount() { return speciesDiversityCount; }
    public void setSpeciesDiversityCount(int speciesDiversityCount) { this.speciesDiversityCount = speciesDiversityCount; }

    public double getMaintenanceComplianceRate() { return maintenanceComplianceRate; }
    public void setMaintenanceComplianceRate(double maintenanceComplianceRate) { this.maintenanceComplianceRate = maintenanceComplianceRate; }

    public String getBreakdownJson() { return breakdownJson; }
    public void setBreakdownJson(String breakdownJson) { this.breakdownJson = breakdownJson; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
