package com.example.project.repository;

import com.example.project.entity.PlantInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantInstanceRepository extends JpaRepository<PlantInstance, Long> {
    Optional<PlantInstance> findByPlantIdentifier(String plantIdentifier);
    Optional<PlantInstance> findByQrCode(String qrCode);
    List<PlantInstance> findByCompanyId(Long companyId);
    List<PlantInstance> findByCompanyLocationId(Long locationId);
    List<PlantInstance> findByBookingId(Long bookingId);
    List<PlantInstance> findByHealthStatus(PlantInstance.HealthStatus healthStatus);
    long countByCompanyId(Long companyId);
    long countByCompanyIdAndHealthStatus(Long companyId, PlantInstance.HealthStatus healthStatus);
}
