package com.example.project.controller;

import com.example.project.dto.PlantTrackingDto;
import com.example.project.service.PlantTrackingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants/instances")
public class PlantTrackingController {

    private final PlantTrackingService plantTrackingService;

    public PlantTrackingController(PlantTrackingService plantTrackingService) {
        this.plantTrackingService = plantTrackingService;
    }

    @GetMapping("/identifier/{identifier}")
    public ResponseEntity<PlantTrackingDto.InstanceResponse> getByIdentifier(@PathVariable String identifier) {
        return ResponseEntity.ok(plantTrackingService.getPlantByIdentifier(identifier));
    }

    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<PlantTrackingDto.InstanceResponse> getByQrCode(@PathVariable String qrCode) {
        return ResponseEntity.ok(plantTrackingService.getPlantByQrCode(qrCode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantTrackingDto.InstanceResponse> getById(@PathVariable Long id,
                                                                     @RequestParam String username) {
        return ResponseEntity.ok(plantTrackingService.getPlantById(id, username));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<PlantTrackingDto.InstanceResponse>> getByCompany(@PathVariable Long companyId,
                                                                                @RequestParam String username) {
        return ResponseEntity.ok(plantTrackingService.getPlantsForCompany(companyId, username));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<PlantTrackingDto.InstanceResponse>> getByLocation(@PathVariable Long locationId,
                                                                                 @RequestParam String username) {
        return ResponseEntity.ok(plantTrackingService.getPlantsForLocation(locationId, username));
    }

    @PostMapping("/{id}/health")
    public ResponseEntity<Void> recordHealth(@PathVariable Long id,
                                             @Valid @RequestBody PlantTrackingDto.HealthLogRequest request,
                                             @RequestParam String username) {
        plantTrackingService.recordHealth(id, request, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/watering")
    public ResponseEntity<Void> recordWatering(@PathVariable Long id,
                                               @RequestBody PlantTrackingDto.WateringRequest request,
                                               @RequestParam String username) {
        plantTrackingService.recordWatering(id, request, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/fertilizer")
    public ResponseEntity<Void> recordFertilizer(@PathVariable Long id,
                                                 @Valid @RequestBody PlantTrackingDto.FertilizerRequest request,
                                                 @RequestParam String username) {
        plantTrackingService.recordFertilizer(id, request, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/replace")
    public ResponseEntity<PlantTrackingDto.InstanceResponse> replacePlant(@PathVariable Long id,
                                                                          @Valid @RequestBody PlantTrackingDto.ReplacementRequest request,
                                                                          @RequestParam String username) {
        return ResponseEntity.ok(plantTrackingService.replacePlant(id, request, username));
    }
}
