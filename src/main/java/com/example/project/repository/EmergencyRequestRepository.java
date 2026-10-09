package com.example.project.repository;

import com.example.project.entity.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {
    List<EmergencyRequest> findByCompanyId(Long companyId);
    List<EmergencyRequest> findByAssignedWorkerId(Long workerId);
    List<EmergencyRequest> findByStatus(EmergencyRequest.Status status);
}
