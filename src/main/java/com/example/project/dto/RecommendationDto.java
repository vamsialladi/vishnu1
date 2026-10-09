package com.example.project.dto;

import java.math.BigDecimal;
import java.util.List;

public class RecommendationDto {

    public static class Request {
        private Double officeAreaSqFt;
        private String sunlightLevel; // LOW, MEDIUM, HIGH, DIRECT_SUN
        private Boolean indoor;
        private BigDecimal budget;
        private String maintenancePreference; // LOW, MEDIUM, HIGH
        private String locationType; // DESK, FLOOR, BALCONY, CORRIDOR, LOBBY
        private Integer desiredPlantCount;

        public Request() {}

        public Double getOfficeAreaSqFt() { return officeAreaSqFt; }
        public void setOfficeAreaSqFt(Double officeAreaSqFt) { this.officeAreaSqFt = officeAreaSqFt; }

        public String getSunlightLevel() { return sunlightLevel; }
        public void setSunlightLevel(String sunlightLevel) { this.sunlightLevel = sunlightLevel; }

        public Boolean getIndoor() { return indoor; }
        public void setIndoor(Boolean indoor) { this.indoor = indoor; }

        public BigDecimal getBudget() { return budget; }
        public void setBudget(BigDecimal budget) { this.budget = budget; }

        public String getMaintenancePreference() { return maintenancePreference; }
        public void setMaintenancePreference(String maintenancePreference) { this.maintenancePreference = maintenancePreference; }

        public String getLocationType() { return locationType; }
        public void setLocationType(String locationType) { this.locationType = locationType; }

        public Integer getDesiredPlantCount() { return desiredPlantCount; }
        public void setDesiredPlantCount(Integer desiredPlantCount) { this.desiredPlantCount = desiredPlantCount; }
    }

    public static class PlantRecommendationItem {
        private ProductResponse plant;
        private double matchScore;
        private List<String> reasons;

        public PlantRecommendationItem() {}

        public PlantRecommendationItem(ProductResponse plant, double matchScore, List<String> reasons) {
            this.plant = plant;
            this.matchScore = matchScore;
            this.reasons = reasons;
        }

        public ProductResponse getPlant() { return plant; }
        public void setPlant(ProductResponse plant) { this.plant = plant; }

        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

        public List<String> getReasons() { return reasons; }
        public void setReasons(List<String> reasons) { this.reasons = reasons; }
    }

    public static class ScoreResponse {
        private int overallScore;
        private int totalPlants;
        private int healthyPlants;
        private double greenCoverageSqFt;
        private int speciesDiversityCount;
        private double maintenanceComplianceRate;
        private String breakdown;
        private String recommendations;

        public ScoreResponse() {}

        public int getOverallScore() { return overallScore; }
        public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

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

        public String getBreakdown() { return breakdown; }
        public void setBreakdown(String breakdown) { this.breakdown = breakdown; }

        public String getRecommendations() { return recommendations; }
        public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    }
}
