package com.example.project.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AdminReportDto {

    public static class DashboardStats {
        private long totalCustomers;
        private long totalWorkers;
        private long totalPlants;
        private long totalBookings;
        private long activeSubscriptions;
        private long pendingInspections;
        private long scheduledInstallations;
        private long openEmergencies;
        private long openComplaints;
        private BigDecimal totalRevenue;
        private Map<String, Long> bookingsByStatus;
        private Map<String, Long> plantHealthDistribution;

        public DashboardStats() {}

        public long getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

        public long getTotalWorkers() { return totalWorkers; }
        public void setTotalWorkers(long totalWorkers) { this.totalWorkers = totalWorkers; }

        public long getTotalPlants() { return totalPlants; }
        public void setTotalPlants(long totalPlants) { this.totalPlants = totalPlants; }

        public long getTotalBookings() { return totalBookings; }
        public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

        public long getActiveSubscriptions() { return activeSubscriptions; }
        public void setActiveSubscriptions(long activeSubscriptions) { this.activeSubscriptions = activeSubscriptions; }

        public long getPendingInspections() { return pendingInspections; }
        public void setPendingInspections(long pendingInspections) { this.pendingInspections = pendingInspections; }

        public long getScheduledInstallations() { return scheduledInstallations; }
        public void setScheduledInstallations(long scheduledInstallations) { this.scheduledInstallations = scheduledInstallations; }

        public long getOpenEmergencies() { return openEmergencies; }
        public void setOpenEmergencies(long openEmergencies) { this.openEmergencies = openEmergencies; }

        public long getOpenComplaints() { return openComplaints; }
        public void setOpenComplaints(long openComplaints) { this.openComplaints = openComplaints; }

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

        public Map<String, Long> getBookingsByStatus() { return bookingsByStatus; }
        public void setBookingsByStatus(Map<String, Long> bookingsByStatus) { this.bookingsByStatus = bookingsByStatus; }

        public Map<String, Long> getPlantHealthDistribution() { return plantHealthDistribution; }
        public void setPlantHealthDistribution(Map<String, Long> plantHealthDistribution) { this.plantHealthDistribution = plantHealthDistribution; }
    }
}
