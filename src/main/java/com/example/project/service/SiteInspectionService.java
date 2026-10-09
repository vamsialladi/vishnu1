package com.example.project.service;

import com.example.project.dto.SiteInspectionDto;
import java.util.List;

public interface SiteInspectionService {
    SiteInspectionDto.Response scheduleInspection(SiteInspectionDto.ScheduleRequest request);
    SiteInspectionDto.Response getInspectionByBookingId(Long bookingId);
    SiteInspectionDto.Response getInspectionById(Long id);
    List<SiteInspectionDto.Response> getInspectionsForWorker(Long workerId);
    SiteInspectionDto.Response recordAssessment(Long inspectionId, SiteInspectionDto.AssessmentRequest request, String workerEmail);
}
