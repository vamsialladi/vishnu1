package com.example.project.serviceimpl;

import com.example.project.dto.EmergencyDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.EmergencyService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmergencyServiceImpl implements EmergencyService {

    private final EmergencyRequestRepository emergencyRepository;
    private final CompanyRepository companyRepository;
    private final PlantInstanceRepository plantInstanceRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    public EmergencyServiceImpl(EmergencyRequestRepository emergencyRepository,
                                CompanyRepository companyRepository,
                                PlantInstanceRepository plantInstanceRepository,
                                WorkerRepository workerRepository,
                                UserRepository userRepository) {
        this.emergencyRepository = emergencyRepository;
        this.companyRepository = companyRepository;
        this.plantInstanceRepository = plantInstanceRepository;
        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).or(() -> userRepository.findByUsername(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void validateCompanyAccess(Company company, User user) {
        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_WORKER) return;
        if (!company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot view emergencies for another company.");
        }
    }

    @Override
    public EmergencyDto.Response createEmergencyRequest(EmergencyDto.CreateRequest request, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + request.getCompanyId()));

        validateCompanyAccess(company, user);

        EmergencyRequest emergency = new EmergencyRequest();
        emergency.setCompany(company);
        emergency.setProblemDescription(request.getProblemDescription());
        emergency.setPhotoUrl(request.getPhotoUrl());
        emergency.setPreferredVisitDate(request.getPreferredVisitDate());

        if (request.getPriority() != null) {
            emergency.setPriority(EmergencyRequest.Priority.valueOf(request.getPriority().toUpperCase()));
        }

        if (request.getPlantInstanceId() != null) {
            PlantInstance instance = plantInstanceRepository.findById(request.getPlantInstanceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plant instance not found: " + request.getPlantInstanceId()));
            emergency.setPlantInstance(instance);
        }

        EmergencyRequest saved = emergencyRepository.save(emergency);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmergencyDto.Response getEmergencyById(Long id, String userEmail) {
        User user = getUser(userEmail);
        EmergencyRequest req = emergencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency request not found: " + id));
        validateCompanyAccess(req.getCompany(), user);
        return mapToResponse(req);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyDto.Response> getEmergenciesForCompany(Long companyId, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
        validateCompanyAccess(company, user);

        return emergencyRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyDto.Response> getEmergenciesForWorker(Long workerId) {
        return emergencyRepository.findByAssignedWorkerId(workerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyDto.Response> getAllEmergencies() {
        return emergencyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmergencyDto.Response updateEmergencyStatus(Long id, EmergencyDto.StatusUpdateRequest request) {
        EmergencyRequest emergency = emergencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency request not found: " + id));

        if (request.getStatus() != null) {
            EmergencyRequest.Status status = EmergencyRequest.Status.valueOf(request.getStatus().toUpperCase());
            emergency.setStatus(status);
            if (status == EmergencyRequest.Status.RESOLVED) {
                emergency.setResolvedAt(LocalDateTime.now());
            }
        }

        if (request.getAssignedWorkerId() != null) {
            Worker worker = workerRepository.findById(request.getAssignedWorkerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Worker not found: " + request.getAssignedWorkerId()));
            emergency.setAssignedWorker(worker);
            emergency.setStatus(EmergencyRequest.Status.ASSIGNED);
        }

        if (request.getResolutionNotes() != null) {
            emergency.setResolutionNotes(request.getResolutionNotes());
        }

        EmergencyRequest saved = emergencyRepository.save(emergency);
        return mapToResponse(saved);
    }

    private EmergencyDto.Response mapToResponse(EmergencyRequest req) {
        EmergencyDto.Response resp = new EmergencyDto.Response();
        resp.setId(req.getId());
        resp.setCompanyId(req.getCompany().getId());
        resp.setCompanyName(req.getCompany().getName());
        if (req.getPlantInstance() != null) {
            resp.setPlantInstanceId(req.getPlantInstance().getId());
            resp.setPlantIdentifier(req.getPlantInstance().getPlantIdentifier());
        }
        resp.setProblemDescription(req.getProblemDescription());
        resp.setPhotoUrl(req.getPhotoUrl());
        resp.setPreferredVisitDate(req.getPreferredVisitDate());
        resp.setPriority(req.getPriority().name());
        resp.setStatus(req.getStatus().name());
        if (req.getAssignedWorker() != null) {
            resp.setAssignedWorkerId(req.getAssignedWorker().getId());
            resp.setAssignedWorkerName(req.getAssignedWorker().getUser().getFullName());
        }
        resp.setResolutionNotes(req.getResolutionNotes());
        resp.setCreatedAt(req.getCreatedAt());
        resp.setResolvedAt(req.getResolvedAt());
        return resp;
    }
}
