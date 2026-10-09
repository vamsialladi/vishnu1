package com.example.project.repository;

import com.example.project.entity.WateringRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WateringRecordRepository extends JpaRepository<WateringRecord, Long> {
    List<WateringRecord> findByPlantInstanceIdOrderByWateredAtDesc(Long plantInstanceId);
}
