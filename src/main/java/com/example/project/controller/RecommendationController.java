package com.example.project.controller;

import com.example.project.dto.RecommendationDto;
import com.example.project.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/plants")
    public ResponseEntity<List<RecommendationDto.PlantRecommendationItem>> getRecommendations(@RequestBody RecommendationDto.Request request) {
        return ResponseEntity.ok(recommendationService.getRecommendations(request));
    }

    @PostMapping("/scores/calculate/{companyId}")
    public ResponseEntity<RecommendationDto.ScoreResponse> calculateScore(@PathVariable Long companyId) {
        return ResponseEntity.ok(recommendationService.calculateAndSaveScore(companyId));
    }

    @GetMapping("/scores/company/{companyId}")
    public ResponseEntity<RecommendationDto.ScoreResponse> getLatestScore(@PathVariable Long companyId,
                                                                         @RequestParam String username) {
        return ResponseEntity.ok(recommendationService.getLatestScore(companyId, username));
    }
}
