package com.example.project.serviceimpl;

import com.example.project.dto.RecommendationDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.RecommendationEngine;
import com.example.project.service.RecommendationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationEngine recommendationEngine;
    private final GreenOfficeScoreRepository scoreRepository;
    private final PlantInstanceRepository plantInstanceRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public RecommendationServiceImpl(RecommendationEngine recommendationEngine,
                                     GreenOfficeScoreRepository scoreRepository,
                                     PlantInstanceRepository plantInstanceRepository,
                                     CompanyRepository companyRepository,
                                     UserRepository userRepository) {
        this.recommendationEngine = recommendationEngine;
        this.scoreRepository = scoreRepository;
        this.plantInstanceRepository = plantInstanceRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).or(() -> userRepository.findByUsername(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto.PlantRecommendationItem> getRecommendations(RecommendationDto.Request request) {
        return recommendationEngine.recommendPlants(request);
    }

    @Override
    public RecommendationDto.ScoreResponse calculateAndSaveScore(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        List<PlantInstance> plants = plantInstanceRepository.findByCompanyId(companyId);
        int totalPlants = plants.size();
        int healthyPlants = (int) plants.stream().filter(p -> p.getHealthStatus() == PlantInstance.HealthStatus.HEALTHY).count();

        Set<Long> distinctSpecies = new HashSet<>();
        for (PlantInstance p : plants) {
            if (p.getProduct() != null) {
                distinctSpecies.add(p.getProduct().getId());
            }
        }
        int diversityCount = distinctSpecies.size();

        double healthRatio = totalPlants > 0 ? (double) healthyPlants / totalPlants : 1.0;
        double complianceRate = 95.0; // Standard maintenance baseline

        // Calculate score from 0-100
        int score = 0;
        if (totalPlants > 0) {
            score = (int) Math.min(100, Math.round((healthRatio * 50) + (Math.min(10, diversityCount) * 3) + (complianceRate * 0.2)));
        }

        GreenOfficeScore greenScore = new GreenOfficeScore();
        greenScore.setCompany(company);
        greenScore.setScore(score);
        greenScore.setTotalPlants(totalPlants);
        greenScore.setHealthyPlants(healthyPlants);
        greenScore.setGreenCoverageSqFt(totalPlants * 12.5); // Estimate 12.5 sq ft per plant
        greenScore.setSpeciesDiversityCount(diversityCount);
        greenScore.setMaintenanceComplianceRate(complianceRate);
        greenScore.setBreakdownJson("{\"healthWeight\": 50, \"diversityWeight\": 30, \"complianceWeight\": 20}");

        StringBuilder recs = new StringBuilder();
        if (totalPlants < 10) {
            recs.append("Add more desk and indoor corridor plants to improve overall oxygen and biophilic index. ");
        }
        if (diversityCount < 3) {
            recs.append("Increase species diversity with air-purifying varieties like Snake Plant or Peace Lily. ");
        }
        if (healthyPlants < totalPlants) {
            recs.append("Schedule a health checkup for distressed plant instances.");
        }
        if (recs.length() == 0) {
            recs.append("Excellent plant maintenance and high green coverage rating!");
        }

        greenScore.setRecommendations(recs.toString());
        greenScore.setCalculatedAt(LocalDateTime.now());

        GreenOfficeScore saved = scoreRepository.save(greenScore);
        return mapToScoreResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationDto.ScoreResponse getLatestScore(Long companyId, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));

        if (user.getRole() != Role.ROLE_ADMIN && !company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot view scores of another company.");
        }

        GreenOfficeScore latest = scoreRepository.findFirstByCompanyIdOrderByCalculatedAtDesc(companyId)
                .orElseGet(() -> {
                    // Calculate initial score if none exists
                    return scoreRepository.save(new GreenOfficeScore());
                });

        return mapToScoreResponse(latest);
    }

    private RecommendationDto.ScoreResponse mapToScoreResponse(GreenOfficeScore s) {
        RecommendationDto.ScoreResponse resp = new RecommendationDto.ScoreResponse();
        resp.setOverallScore(s.getScore());
        resp.setTotalPlants(s.getTotalPlants());
        resp.setHealthyPlants(s.getHealthyPlants());
        resp.setGreenCoverageSqFt(s.getGreenCoverageSqFt());
        resp.setSpeciesDiversityCount(s.getSpeciesDiversityCount());
        resp.setMaintenanceComplianceRate(s.getMaintenanceComplianceRate());
        resp.setBreakdown(s.getBreakdownJson());
        resp.setRecommendations(s.getRecommendations());
        return resp;
    }
}
