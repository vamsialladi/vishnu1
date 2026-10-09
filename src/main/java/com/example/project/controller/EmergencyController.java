package com.example.project.controller;

import com.example.project.dto.EmergencyDto;
import com.example.project.service.EmergencyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergencies")
public class EmergencyController {

    private final EmergencyService emergencyService;

    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
    }

    @PostMapping
    public ResponseEntity<EmergencyDto.Response> createEmergency(@Valid @RequestBody EmergencyDto.CreateRequest request,
                                                                 @RequestParam String username) {
        return new ResponseEntity<>(emergencyService.createEmergencyRequest(request, username), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyDto.Response> getById(@PathVariable Long id,
                                                         @RequestParam String username) {
        return ResponseEntity.ok(emergencyService.getEmergencyById(id, username));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<EmergencyDto.Response>> getByCompany(@PathVariable Long companyId,
                                                                    @RequestParam String username) {
        return ResponseEntity.ok(emergencyService.getEmergenciesForCompany(companyId, username));
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<EmergencyDto.Response>> getByWorker(@PathVariable Long workerId) {
        return ResponseEntity.ok(emergencyService.getEmergenciesForWorker(workerId));
    }

    @GetMapping
    public ResponseEntity<List<EmergencyDto.Response>> getAll() {
        return ResponseEntity.ok(emergencyService.getAllEmergencies());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EmergencyDto.Response> updateStatus(@PathVariable Long id,
                                                              @RequestBody EmergencyDto.StatusUpdateRequest request) {
        return ResponseEntity.ok(emergencyService.updateEmergencyStatus(id, request));
    }
}
