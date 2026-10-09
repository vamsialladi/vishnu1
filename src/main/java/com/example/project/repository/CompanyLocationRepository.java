package com.example.project.repository;

import com.example.project.entity.CompanyLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyLocationRepository extends JpaRepository<CompanyLocation, Long> {
    List<CompanyLocation> findByCompanyId(Long companyId);
    Optional<CompanyLocation> findByIdAndCompanyId(Long id, Long companyId);
}
