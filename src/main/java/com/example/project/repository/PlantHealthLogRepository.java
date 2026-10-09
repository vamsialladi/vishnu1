package com.example.project.repository;

import com.example.project.entity.PlantHealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantHealthLogRepository extends JpaRepository<PlantHealthLog, Long> {
    List<PlantHealthLog> findByPlantInstanceIdOrderByLoggedAtDesc(Long plantInstanceId);
}
