package com.example.project.service;

import com.example.project.dto.RecommendationDto;
import com.example.project.entity.*;
import com.example.project.repository.CompanyRepository;
import com.example.project.repository.GreenOfficeScoreRepository;
import com.example.project.repository.PlantInstanceRepository;
import com.example.project.repository.UserRepository;
import com.example.project.serviceimpl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationEngine recommendationEngine;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PlantInstanceRepository plantInstanceRepository;

    @Mock
    private GreenOfficeScoreRepository scoreRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private Company company;

    @BeforeEach
    void setUp() {
        User owner = new User("owner", "owner@example.com", "pass", "Jane Owner", Role.ROLE_CUSTOMER);
        owner.setId(2L);

        company = new Company("Acme Tech", "REG-888", "owner@example.com", owner);
        company.setId(10L);
    }

    @Test
    @DisplayName("Should return ranked plant recommendations")
    void shouldReturnRecommendations() {
        RecommendationDto.Request req = new RecommendationDto.Request();
        req.setOfficeAreaSqFt(500.0);
        req.setSunlightLevel("BRIGHT");

        RecommendationDto.PlantRecommendationItem item = new RecommendationDto.PlantRecommendationItem(
                1L, "Peace Lily", "Low maintenance", BigDecimal.valueOf(450), 92.0, List.of("Excellent air purifier"));

        when(recommendationEngine.recommendPlants(req)).thenReturn(List.of(item));

        List<RecommendationDto.PlantRecommendationItem> result = recommendationService.getRecommendations(req);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Peace Lily");
        assertThat(result.get(0).getMatchScore()).isEqualTo(92.0);
    }

    @Test
    @DisplayName("Should calculate and save Green Office Score from 0 to 100")
    void shouldCalculateGreenOfficeScore() {
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));

        Product p1 = new Product("Snake Plant", "Indoor", BigDecimal.valueOf(500), 10, "Indoor", "SKU-1");
        p1.setId(1L);

        PlantInstance inst1 = new PlantInstance("PLANT-1", "QR-1", p1, company, null, null, null);
        inst1.setHealthStatus(PlantInstance.HealthStatus.HEALTHY);

        PlantInstance inst2 = new PlantInstance("PLANT-2", "QR-2", p1, company, null, null, null);
        inst2.setHealthStatus(PlantInstance.HealthStatus.HEALTHY);

        when(plantInstanceRepository.findByCompanyId(10L)).thenReturn(List.of(inst1, inst2));
        when(scoreRepository.save(any(GreenOfficeScore.class))).thenAnswer(inv -> inv.getArgument(0));

        RecommendationDto.ScoreResponse score = recommendationService.calculateAndSaveScore(10L);

        assertThat(score).isNotNull();
        assertThat(score.getTotalScore()).isBetween(0, 100);
        assertThat(score.getTotalPlants()).isEqualTo(2);
        assertThat(score.getHealthyPlants()).isEqualTo(2);
    }
}
