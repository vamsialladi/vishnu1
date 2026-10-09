package com.example.project.serviceimpl;

import com.example.project.dto.SiteInspectionDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.BookingRepository;
import com.example.project.repository.SiteInspectionRepository;
import com.example.project.repository.UserRepository;
import com.example.project.repository.WorkerRepository;
import com.example.project.service.SiteInspectionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SiteInspectionServiceImpl implements SiteInspectionService {

    private final SiteInspectionRepository siteInspectionRepository;
    private final BookingRepository bookingRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    public SiteInspectionServiceImpl(SiteInspectionRepository siteInspectionRepository,
                                    BookingRepository bookingRepository,
                                    WorkerRepository workerRepository,
                                    UserRepository userRepository) {
        this.siteInspectionRepository = siteInspectionRepository;
        this.bookingRepository = bookingRepository;
        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SiteInspectionDto.Response scheduleInspection(SiteInspectionDto.ScheduleRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        SiteInspection inspection = siteInspectionRepository.findByBookingId(booking.getId())
                .orElseGet(() -> {
                    SiteInspection insp = new SiteInspection();
                    insp.setBooking(booking);
                    return insp;
                });

        if (request.getAssignedWorkerId() != null) {
            Worker worker = workerRepository.findById(request.getAssignedWorkerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + request.getAssignedWorkerId()));
            inspection.setAssignedWorker(worker);
        }

        inspection.setScheduledDate(request.getScheduledDate());
        inspection.setStatus(SiteInspection.InspectionStatus.SCHEDULED);

        SiteInspection saved = siteInspectionRepository.save(inspection);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SiteInspectionDto.Response getInspectionByBookingId(Long bookingId) {
        SiteInspection inspection = siteInspectionRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found for booking id: " + bookingId));
        return mapToResponse(inspection);
    }

    @Override
    @Transactional(readOnly = true)
    public SiteInspectionDto.Response getInspectionById(Long id) {
        SiteInspection inspection = siteInspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found with id: " + id));
        return mapToResponse(inspection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteInspectionDto.Response> getInspectionsForWorker(Long workerId) {
        return siteInspectionRepository.findByAssignedWorkerId(workerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SiteInspectionDto.Response recordAssessment(Long inspectionId, SiteInspectionDto.AssessmentRequest request, String workerEmail) {
        User user = userRepository.findByEmail(workerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + workerEmail));

        SiteInspection inspection = siteInspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found with id: " + inspectionId));

        if (user.getRole() != Role.ROLE_ADMIN) {
            if (inspection.getAssignedWorker() == null ||
                !inspection.getAssignedWorker().getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access denied: You are not the assigned worker for this inspection.");
            }
        }

        inspection.setSpaceAssessment(request.getSpaceAssessment());
        inspection.setSunlightAssessment(request.getSunlightAssessment());
        inspection.setTemperatureAssessment(request.getTemperatureAssessment());
        inspection.setHumidityAssessment(request.getHumidityAssessment());
        if (request.getIndoorFeasible() != null) {
            inspection.setIndoorFeasible(request.getIndoorFeasible());
        }
        inspection.setPlantRecommendations(request.getPlantRecommendations());
        inspection.setInspectionNotes(request.getInspectionNotes());
        inspection.setStatus(SiteInspection.InspectionStatus.COMPLETED);
        inspection.setCompletedDate(LocalDateTime.now());

        Booking booking = inspection.getBooking();
        booking.setStatus(Booking.BookingStatus.SITE_INSPECTION_COMPLETED);
        bookingRepository.save(booking);

        SiteInspection saved = siteInspectionRepository.save(inspection);
        return mapToResponse(saved);
    }

    private SiteInspectionDto.Response mapToResponse(SiteInspection inspection) {
        SiteInspectionDto.Response resp = new SiteInspectionDto.Response();
        resp.setId(inspection.getId());
        resp.setBookingId(inspection.getBooking().getId());
        resp.setBookingNumber(inspection.getBooking().getBookingNumber());
        if (inspection.getAssignedWorker() != null) {
            resp.setAssignedWorkerId(inspection.getAssignedWorker().getId());
            resp.setAssignedWorkerName(inspection.getAssignedWorker().getUser().getFullName());
        }
        resp.setScheduledDate(inspection.getScheduledDate());
        resp.setCompletedDate(inspection.getCompletedDate());
        resp.setStatus(inspection.getStatus().name());
        resp.setSpaceAssessment(inspection.getSpaceAssessment());
        resp.setSunlightAssessment(inspection.getSunlightAssessment());
        resp.setTemperatureAssessment(inspection.getTemperatureAssessment());
        resp.setHumidityAssessment(inspection.getHumidityAssessment());
        resp.setIndoorFeasible(inspection.isIndoorFeasible());
        resp.setPlantRecommendations(inspection.getPlantRecommendations());
        resp.setInspectionNotes(inspection.getInspectionNotes());
        return resp;
    }
}
