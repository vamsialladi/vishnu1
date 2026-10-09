package com.example.project.controller;

import com.example.project.dto.SiteInspectionDto;
import com.example.project.service.SiteInspectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspections")
public class SiteInspectionController {

    private final SiteInspectionService inspectionService;

    public SiteInspectionController(SiteInspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<SiteInspectionDto.Response> scheduleInspection(@Valid @RequestBody SiteInspectionDto.ScheduleRequest request) {
        return new ResponseEntity<>(inspectionService.scheduleInspection(request), HttpStatus.CREATED);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<SiteInspectionDto.Response> getByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(inspectionService.getInspectionByBookingId(bookingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteInspectionDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inspectionService.getInspectionById(id));
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<SiteInspectionDto.Response>> getByWorkerId(@PathVariable Long workerId) {
        return ResponseEntity.ok(inspectionService.getInspectionsForWorker(workerId));
    }

    @PostMapping("/{id}/assessment")
    public ResponseEntity<SiteInspectionDto.Response> recordAssessment(@PathVariable Long id,
                                                                       @RequestBody SiteInspectionDto.AssessmentRequest request,
                                                                       @RequestParam String username) {
        return ResponseEntity.ok(inspectionService.recordAssessment(id, request, username));
    }
}
