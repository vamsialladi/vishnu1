package com.example.project.repository;

import com.example.project.entity.PlantReplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantReplacementRepository extends JpaRepository<PlantReplacement, Long> {
    List<PlantReplacement> findByOldPlantInstanceId(Long oldPlantId);
    List<PlantReplacement> findByNewPlantInstanceId(Long newPlantId);
}
