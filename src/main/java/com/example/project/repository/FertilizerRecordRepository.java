package com.example.project.repository;

import com.example.project.entity.FertilizerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FertilizerRecordRepository extends JpaRepository<FertilizerRecord, Long> {
    List<FertilizerRecord> findByPlantInstanceIdOrderByAppliedAtDesc(Long plantInstanceId);
}
