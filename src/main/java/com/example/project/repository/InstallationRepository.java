package com.example.project.repository;

import com.example.project.entity.Installation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstallationRepository extends JpaRepository<Installation, Long> {
    Optional<Installation> findByBookingId(Long bookingId);
    List<Installation> findByAssignedWorkerId(Long workerId);
    List<Installation> findByStatus(Installation.InstallationStatus status);
}
