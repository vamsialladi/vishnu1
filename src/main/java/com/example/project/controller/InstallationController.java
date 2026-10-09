package com.example.project.controller;

import com.example.project.dto.InstallationDto;
import com.example.project.service.InstallationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/installations")
public class InstallationController {

    private final InstallationService installationService;

    public InstallationController(InstallationService installationService) {
        this.installationService = installationService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<InstallationDto.Response> scheduleInstallation(@Valid @RequestBody InstallationDto.ScheduleRequest request) {
        return new ResponseEntity<>(installationService.scheduleInstallation(request), HttpStatus.CREATED);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<InstallationDto.Response> getByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(installationService.getInstallationByBookingId(bookingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstallationDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(installationService.getInstallationById(id));
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<InstallationDto.Response>> getByWorkerId(@PathVariable Long workerId) {
        return ResponseEntity.ok(installationService.getInstallationsForWorker(workerId));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<InstallationDto.Response> startInstallation(@PathVariable Long id,
                                                                      @RequestParam String username) {
        return ResponseEntity.ok(installationService.startInstallation(id, username));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<InstallationDto.Response> completeInstallation(@PathVariable Long id,
                                                                         @RequestBody InstallationDto.CompleteRequest request,
                                                                         @RequestParam String username) {
        return ResponseEntity.ok(installationService.completeInstallation(id, request, username));
    }
}
