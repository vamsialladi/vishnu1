package com.example.project.service;

import com.example.project.dto.RecommendationDto;
import java.util.List;

public interface RecommendationService {
    List<RecommendationDto.PlantRecommendationItem> getRecommendations(RecommendationDto.Request request);
    RecommendationDto.ScoreResponse calculateAndSaveScore(Long companyId);
    RecommendationDto.ScoreResponse getLatestScore(Long companyId, String userEmail);
}
