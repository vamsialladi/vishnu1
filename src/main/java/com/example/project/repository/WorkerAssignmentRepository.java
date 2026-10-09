package com.example.project.repository;

import com.example.project.entity.WorkerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerAssignmentRepository extends JpaRepository<WorkerAssignment, Long> {
    List<WorkerAssignment> findByWorkerId(Long workerId);
    List<WorkerAssignment> findByBookingId(Long bookingId);
    List<WorkerAssignment> findByWorkerIdAndStatus(Long workerId, String status);
}
