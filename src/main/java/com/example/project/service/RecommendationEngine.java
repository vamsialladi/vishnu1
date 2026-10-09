package com.example.project.service;

import com.example.project.dto.RecommendationDto;
import java.util.List;

public interface RecommendationEngine {
    List<RecommendationDto.PlantRecommendationItem> recommendPlants(RecommendationDto.Request request);
}
