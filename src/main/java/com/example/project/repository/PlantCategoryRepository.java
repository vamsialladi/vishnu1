package com.example.project.repository;

import com.example.project.entity.PlantCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantCategoryRepository extends JpaRepository<PlantCategory, Long> {
    Optional<PlantCategory> findByName(String name);
    boolean existsByName(String name);
}
