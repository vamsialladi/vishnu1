package com.example.project.service;

import com.example.project.dto.InstallationDto;
import java.util.List;

public interface InstallationService {
    InstallationDto.Response scheduleInstallation(InstallationDto.ScheduleRequest request);
    InstallationDto.Response getInstallationByBookingId(Long bookingId);
    InstallationDto.Response getInstallationById(Long id);
    List<InstallationDto.Response> getInstallationsForWorker(Long workerId);
    InstallationDto.Response startInstallation(Long installationId, String workerEmail);
    InstallationDto.Response completeInstallation(Long installationId, InstallationDto.CompleteRequest request, String workerEmail);
}
