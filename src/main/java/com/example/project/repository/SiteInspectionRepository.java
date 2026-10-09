package com.example.project.repository;

import com.example.project.entity.SiteInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteInspectionRepository extends JpaRepository<SiteInspection, Long> {
    Optional<SiteInspection> findByBookingId(Long bookingId);
    List<SiteInspection> findByAssignedWorkerId(Long workerId);
    List<SiteInspection> findByStatus(SiteInspection.InspectionStatus status);
}
