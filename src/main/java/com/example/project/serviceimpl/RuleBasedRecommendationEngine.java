package com.example.project.serviceimpl;

import com.example.project.dto.ProductResponse;
import com.example.project.dto.RecommendationDto;
import com.example.project.entity.Product;
import com.example.project.repository.ProductRepository;
import com.example.project.service.RecommendationEngine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RuleBasedRecommendationEngine implements RecommendationEngine {

    private final ProductRepository productRepository;

    public RuleBasedRecommendationEngine(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<RecommendationDto.PlantRecommendationItem> recommendPlants(RecommendationDto.Request request) {
        // Get all active products
        List<Product> activePlants = productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());

        List<RecommendationDto.PlantRecommendationItem> scoredItems = new ArrayList<>();

        for (Product p : activePlants) {
            double score = 50.0; // Base score
            List<String> reasons = new ArrayList<>();

            // Indoor/Outdoor match
            if (request.getIndoor() != null) {
                if (p.isIndoor() == request.getIndoor()) {
                    score += 20;
                    reasons.add(request.getIndoor() ? "Optimal for indoor office air & lighting" : "Ideal for outdoor courtyard / balcony");
                } else {
                    score -= 30;
                }
            }

            // Sunlight level match
            if (request.getSunlightLevel() != null && p.getSunlightRequirement() != null) {
                if (p.getSunlightRequirement().equalsIgnoreCase(request.getSunlightLevel())) {
                    score += 15;
                    reasons.add("Matches sunlight exposure requirement: " + request.getSunlightLevel());
                }
            }

            // Maintenance level match
            if (request.getMaintenancePreference() != null && p.getMaintenanceLevel() != null) {
                if (p.getMaintenanceLevel().equalsIgnoreCase(request.getMaintenancePreference())) {
                    score += 15;
                    reasons.add("Fits maintenance preference: " + request.getMaintenancePreference());
                }
            }

            // Budget match
            if (request.getBudget() != null && p.getPrice() != null) {
                if (p.getPrice().compareTo(request.getBudget()) <= 0) {
                    score += 10;
                    reasons.add("Comfortably within target budget per plant");
                }
            }

            if (score > 30) {
                scoredItems.add(new RecommendationDto.PlantRecommendationItem(mapToProductResponse(p), score, reasons));
            }
        }

        // Sort descending by match score
        scoredItems.sort(Comparator.comparingDouble(RecommendationDto.PlantRecommendationItem::getMatchScore).reversed());

        int limit = request.getDesiredPlantCount() != null && request.getDesiredPlantCount() > 0
                ? request.getDesiredPlantCount() : 10;

        return scoredItems.stream().limit(limit).collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product p) {
        ProductResponse resp = new ProductResponse();
        resp.setId(p.getId());
        resp.setName(p.getName());
        resp.setScientificName(p.getScientificName());
        resp.setDescription(p.getDescription());
        resp.setPrice(p.getPrice());
        resp.setRentalPrice(p.getRentalPrice());
        resp.setInstallationCharge(p.getInstallationCharge());
        resp.setQuantity(p.getQuantity());
        resp.setSku(p.getSku());
        resp.setCategory(p.getCategory());
        resp.setIndoor(p.isIndoor());
        resp.setSunlightRequirement(p.getSunlightRequirement());
        resp.setWaterRequirement(p.getWaterRequirement());
        resp.setMaintenanceLevel(p.getMaintenanceLevel());
        resp.setActive(p.isActive());
        return resp;
    }
}
