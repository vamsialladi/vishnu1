package com.example.project.serviceimpl;

import com.example.project.dto.PlantTrackingDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.PlantTrackingService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlantTrackingServiceImpl implements PlantTrackingService {

    private final PlantInstanceRepository instanceRepository;
    private final PlantHealthLogRepository healthLogRepository;
    private final WateringRecordRepository wateringRepository;
    private final FertilizerRecordRepository fertilizerRepository;
    private final PlantReplacementRepository replacementRepository;
    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final CompanyLocationRepository locationRepository;
    private final UserRepository userRepository;

    public PlantTrackingServiceImpl(PlantInstanceRepository instanceRepository,
                                   PlantHealthLogRepository healthLogRepository,
                                   WateringRecordRepository wateringRepository,
                                   FertilizerRecordRepository fertilizerRepository,
                                   PlantReplacementRepository replacementRepository,
                                   ProductRepository productRepository,
                                   CompanyRepository companyRepository,
                                   CompanyLocationRepository locationRepository,
                                   UserRepository userRepository) {
        this.instanceRepository = instanceRepository;
        this.healthLogRepository = healthLogRepository;
        this.wateringRepository = wateringRepository;
        this.fertilizerRepository = fertilizerRepository;
        this.replacementRepository = replacementRepository;
        this.productRepository = productRepository;
        this.companyRepository = companyRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).or(() -> userRepository.findByUsername(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void validateCompanyAccess(Company company, User user) {
        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_WORKER) return;
        if (!company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot access plants belonging to another company.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PlantTrackingDto.InstanceResponse getPlantByIdentifier(String identifier) {
        PlantInstance instance = instanceRepository.findByPlantIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with identifier: " + identifier));
        return mapToInstanceResponse(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantTrackingDto.InstanceResponse getPlantByQrCode(String qrCode) {
        PlantInstance instance = instanceRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with QR code: " + qrCode));
        return mapToInstanceResponse(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantTrackingDto.InstanceResponse getPlantById(Long id, String userEmail) {
        User user = getUser(userEmail);
        PlantInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant instance not found with id: " + id));
        validateCompanyAccess(instance.getCompany(), user);
        return mapToInstanceResponse(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantTrackingDto.InstanceResponse> getPlantsForCompany(Long companyId, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
        validateCompanyAccess(company, user);

        return instanceRepository.findByCompanyId(companyId).stream()
                .map(this::mapToInstanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantTrackingDto.InstanceResponse> getPlantsForLocation(Long locationId, String userEmail) {
        User user = getUser(userEmail);
        CompanyLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found: " + locationId));
        validateCompanyAccess(location.getCompany(), user);

        return instanceRepository.findByCompanyLocationId(locationId).stream()
                .map(this::mapToInstanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void recordHealth(Long plantId, PlantTrackingDto.HealthLogRequest request, String workerEmail) {
        User user = getUser(workerEmail);
        PlantInstance instance = instanceRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant instance not found: " + plantId));

        PlantInstance.HealthStatus status = PlantInstance.HealthStatus.valueOf(request.getHealthStatus().toUpperCase());
        instance.setHealthStatus(status);
        instance.setLastServiceDate(LocalDateTime.now());
        instanceRepository.save(instance);

        PlantHealthLog log = new PlantHealthLog();
        log.setPlantInstance(instance);
        log.setHealthStatus(status);
        log.setDiseaseNotes(request.getDiseaseNotes());
        log.setPestNotes(request.getPestNotes());
        log.setGeneralNotes(request.getGeneralNotes());
        log.setPhotoUrl(request.getPhotoUrl());
        if (user.getRole() == Role.ROLE_WORKER || user.getRole() == Role.ROLE_ADMIN) {
            log.setRecordedByUser(user);
        }
        healthLogRepository.save(log);
    }

    @Override
    public void recordWatering(Long plantId, PlantTrackingDto.WateringRequest request, String workerEmail) {
        User user = getUser(workerEmail);
        PlantInstance instance = instanceRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant instance not found: " + plantId));

        instance.setLastServiceDate(LocalDateTime.now());
        instanceRepository.save(instance);

        WateringRecord record = new WateringRecord();
        record.setPlantInstance(instance);
        if (request.getAmountLiters() != null) record.setAmountLiters(request.getAmountLiters());
        record.setNotes(request.getNotes());
        record.setRecordedByUser(user);
        wateringRepository.save(record);
    }

    @Override
    public void recordFertilizer(Long plantId, PlantTrackingDto.FertilizerRequest request, String workerEmail) {
        User user = getUser(workerEmail);
        PlantInstance instance = instanceRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant instance not found: " + plantId));

        instance.setLastServiceDate(LocalDateTime.now());
        instanceRepository.save(instance);

        FertilizerRecord record = new FertilizerRecord();
        record.setPlantInstance(instance);
        record.setFertilizerType(request.getFertilizerType());
        if (request.getQuantityGrams() != null) record.setQuantityGrams(request.getQuantityGrams());
        record.setNotes(request.getNotes());
        record.setRecordedByUser(user);
        fertilizerRepository.save(record);
    }

    @Override
    public PlantTrackingDto.InstanceResponse replacePlant(Long oldPlantId, PlantTrackingDto.ReplacementRequest request, String workerEmail) {
        User user = getUser(workerEmail);
        PlantInstance oldInstance = instanceRepository.findById(oldPlantId)
                .orElseThrow(() -> new ResourceNotFoundException("Old plant instance not found: " + oldPlantId));

        Product newProduct = productRepository.findById(request.getNewProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getNewProductId()));

        // Mark old plant as REPLACED - do not delete history
        oldInstance.setHealthStatus(PlantInstance.HealthStatus.REPLACED);
        instanceRepository.save(oldInstance);

        // Create new plant instance
        String newPlantCode = "PLANT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PlantInstance newInstance = new PlantInstance();
        newInstance.setPlantIdentifier(newPlantCode);
        newInstance.setQrCode("QR-" + newPlantCode);
        newInstance.setProduct(newProduct);
        newInstance.setCompany(oldInstance.getCompany());
        newInstance.setCompanyLocation(oldInstance.getCompanyLocation());
        newInstance.setBooking(oldInstance.getBooking());
        newInstance.setInstallationDate(LocalDate.now());
        newInstance.setHealthStatus(PlantInstance.HealthStatus.HEALTHY);

        PlantInstance savedNew = instanceRepository.save(newInstance);

        // Record replacement audit record
        PlantReplacement replacement = new PlantReplacement();
        replacement.setOldPlantInstance(oldInstance);
        replacement.setNewPlantInstance(savedNew);
        replacement.setReason(request.getReason());
        replacement.setReplacementCost(request.getReplacementCost());
        replacement.setCoveredByPlan(request.isCoveredByPlan());
        replacement.setReplacedByUser(user);
        replacementRepository.save(replacement);

        return mapToInstanceResponse(savedNew);
    }

    private PlantTrackingDto.InstanceResponse mapToInstanceResponse(PlantInstance pi) {
        PlantTrackingDto.InstanceResponse resp = new PlantTrackingDto.InstanceResponse();
        resp.setId(pi.getId());
        resp.setPlantIdentifier(pi.getPlantIdentifier());
        resp.setQrCode(pi.getQrCode());
        resp.setProductId(pi.getProduct().getId());
        resp.setProductName(pi.getProduct().getName());
        resp.setCompanyId(pi.getCompany().getId());
        resp.setCompanyName(pi.getCompany().getName());
        if (pi.getCompanyLocation() != null) {
            resp.setLocationId(pi.getCompanyLocation().getId());
            resp.setLocationName(pi.getCompanyLocation().getLocationName());
        }
        if (pi.getBooking() != null) resp.setBookingId(pi.getBooking().getId());
        resp.setInstallationDate(pi.getInstallationDate());
        resp.setHealthStatus(pi.getHealthStatus().name());
        resp.setLastServiceDate(pi.getLastServiceDate());
        resp.setNotes(pi.getNotes());
        return resp;
    }
}
