package com.example.project.repository;

import com.example.project.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByCompanyId(Long companyId);
    List<Subscription> findByStatus(Subscription.SubscriptionStatus status);
    List<Subscription> findByStatusAndNextServiceDateLessThanEqual(Subscription.SubscriptionStatus status, LocalDate date);
    Optional<Subscription> findByBookingId(Long bookingId);
}
