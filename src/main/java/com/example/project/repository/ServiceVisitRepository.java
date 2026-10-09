package com.example.project.repository;

import com.example.project.entity.ServiceVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceVisitRepository extends JpaRepository<ServiceVisit, Long> {
    List<ServiceVisit> findBySubscriptionId(Long subscriptionId);
    List<ServiceVisit> findByCompanyId(Long companyId);
    List<ServiceVisit> findByAssignedWorkerId(Long workerId);
    List<ServiceVisit> findByScheduledDateAndStatus(LocalDate scheduledDate, ServiceVisit.VisitStatus status);
    List<ServiceVisit> findByScheduledDateLessThanAndStatus(LocalDate scheduledDate, ServiceVisit.VisitStatus status);
}
