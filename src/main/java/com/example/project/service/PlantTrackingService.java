package com.example.project.service;

import com.example.project.dto.PlantTrackingDto;
import java.util.List;

public interface PlantTrackingService {
    PlantTrackingDto.InstanceResponse getPlantByIdentifier(String identifier);
    PlantTrackingDto.InstanceResponse getPlantByQrCode(String qrCode);
    PlantTrackingDto.InstanceResponse getPlantById(Long id, String userEmail);
    List<PlantTrackingDto.InstanceResponse> getPlantsForCompany(Long companyId, String userEmail);
    List<PlantTrackingDto.InstanceResponse> getPlantsForLocation(Long locationId, String userEmail);

    void recordHealth(Long plantId, PlantTrackingDto.HealthLogRequest request, String workerEmail);
    void recordWatering(Long plantId, PlantTrackingDto.WateringRequest request, String workerEmail);
    void recordFertilizer(Long plantId, PlantTrackingDto.FertilizerRequest request, String workerEmail);
    PlantTrackingDto.InstanceResponse replacePlant(Long oldPlantId, PlantTrackingDto.ReplacementRequest request, String workerEmail);
}
