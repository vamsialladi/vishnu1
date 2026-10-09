package com.example.project.service;

import com.example.project.dto.EmergencyDto;
import java.util.List;

public interface EmergencyService {
    EmergencyDto.Response createEmergencyRequest(EmergencyDto.CreateRequest request, String userEmail);
    EmergencyDto.Response getEmergencyById(Long id, String userEmail);
    List<EmergencyDto.Response> getEmergenciesForCompany(Long companyId, String userEmail);
    List<EmergencyDto.Response> getEmergenciesForWorker(Long workerId);
    List<EmergencyDto.Response> getAllEmergencies();
    EmergencyDto.Response updateEmergencyStatus(Long id, EmergencyDto.StatusUpdateRequest request);
}
