package com.example.project.serviceimpl;

import com.example.project.dto.MaintenanceDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.MaintenanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenancePlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ServiceVisitRepository visitRepository;
    private final CompanyRepository companyRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    public MaintenanceServiceImpl(MaintenancePlanRepository planRepository,
                                  SubscriptionRepository subscriptionRepository,
                                  ServiceVisitRepository visitRepository,
                                  CompanyRepository companyRepository,
                                  WorkerRepository workerRepository,
                                  UserRepository userRepository) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.visitRepository = visitRepository;
        this.companyRepository = companyRepository;
        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void validateCompanyAccess(Company company, User user) {
        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_WORKER) return;
        if (!company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot access subscriptions of another company.");
        }
    }

    @Override
    public MaintenanceDto.PlanResponse createPlan(MaintenanceDto.PlanRequest request) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setMonthlyPrice(request.getMonthlyPrice());
        if (request.getFrequency() != null) {
            plan.setFrequency(MaintenancePlan.Frequency.valueOf(request.getFrequency().toUpperCase()));
        }
        plan.setIncludedServices(request.getIncludedServices());
        plan.setReplacementSupport(request.isReplacementSupport());
        plan.setEmergencySupport(request.isEmergencySupport());
        plan.setConsultationSupport(request.isConsultationSupport());

        MaintenancePlan saved = planRepository.save(plan);
        return mapToPlanResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDto.PlanResponse> getAllActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(this::mapToPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceDto.PlanResponse getPlanById(Long id) {
        MaintenancePlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance plan not found: " + id));
        return mapToPlanResponse(plan);
    }

    @Override
    public MaintenanceDto.PlanResponse updatePlan(Long id, MaintenanceDto.PlanRequest request) {
        MaintenancePlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance plan not found: " + id));
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setMonthlyPrice(request.getMonthlyPrice());
        if (request.getFrequency() != null) {
            plan.setFrequency(MaintenancePlan.Frequency.valueOf(request.getFrequency().toUpperCase()));
        }
        plan.setIncludedServices(request.getIncludedServices());
        plan.setReplacementSupport(request.isReplacementSupport());
        plan.setEmergencySupport(request.isEmergencySupport());
        plan.setConsultationSupport(request.isConsultationSupport());

        MaintenancePlan saved = planRepository.save(plan);
        return mapToPlanResponse(saved);
    }

    @Override
    public void deletePlan(Long id) {
        MaintenancePlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance plan not found: " + id));
        plan.setActive(false);
        planRepository.save(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDto.SubscriptionResponse> getSubscriptionsForCompany(Long companyId, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
        validateCompanyAccess(company, user);

        return subscriptionRepository.findByCompanyId(companyId).stream()
                .map(this::mapToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceDto.SubscriptionResponse getSubscriptionById(Long id, String userEmail) {
        User user = getUser(userEmail);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        validateCompanyAccess(subscription.getCompany(), user);
        return mapToSubscriptionResponse(subscription);
    }

    @Override
    public void pauseSubscription(Long id, String userEmail) {
        User user = getUser(userEmail);
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        validateCompanyAccess(sub.getCompany(), user);
        sub.setStatus(Subscription.SubscriptionStatus.PAUSED);
        subscriptionRepository.save(sub);
    }

    @Override
    public void activateSubscription(Long id, String userEmail) {
        User user = getUser(userEmail);
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        validateCompanyAccess(sub.getCompany(), user);
        sub.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(sub);
    }

    @Override
    public void cancelSubscription(Long id, String userEmail) {
        User user = getUser(userEmail);
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        validateCompanyAccess(sub.getCompany(), user);
        sub.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(sub);
    }

    @Override
    public MaintenanceDto.ServiceVisitResponse scheduleVisit(Long subscriptionId, LocalDate visitDate, Long workerId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + subscriptionId));

        ServiceVisit visit = new ServiceVisit();
        visit.setSubscription(sub);
        visit.setCompany(sub.getCompany());
        visit.setScheduledDate(visitDate);
        visit.setStatus(ServiceVisit.VisitStatus.SCHEDULED);

        if (workerId != null) {
            Worker worker = workerRepository.findById(workerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Worker not found: " + workerId));
            visit.setAssignedWorker(worker);
        }

        ServiceVisit saved = visitRepository.save(visit);
        return mapToVisitResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDto.ServiceVisitResponse> getVisitsForSubscription(Long subscriptionId, String userEmail) {
        User user = getUser(userEmail);
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + subscriptionId));
        validateCompanyAccess(sub.getCompany(), user);

        return visitRepository.findBySubscriptionId(subscriptionId).stream()
                .map(this::mapToVisitResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDto.ServiceVisitResponse> getVisitsForWorker(Long workerId) {
        return visitRepository.findByAssignedWorkerId(workerId).stream()
                .map(this::mapToVisitResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceDto.ServiceVisitResponse recordArrival(Long visitId, String workerEmail) {
        User user = getUser(workerEmail);
        ServiceVisit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found: " + visitId));

        if (user.getRole() != Role.ROLE_ADMIN && (visit.getAssignedWorker() == null || !visit.getAssignedWorker().getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Access denied: You are not assigned to this visit.");
        }

        visit.setArrivalTime(LocalDateTime.now());
        visit.setStatus(ServiceVisit.VisitStatus.IN_PROGRESS);
        ServiceVisit saved = visitRepository.save(visit);
        return mapToVisitResponse(saved);
    }

    @Override
    public MaintenanceDto.ServiceVisitResponse completeVisit(Long visitId, String notes, String photosJson, String workerEmail) {
        User user = getUser(workerEmail);
        ServiceVisit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found: " + visitId));

        if (user.getRole() != Role.ROLE_ADMIN && (visit.getAssignedWorker() == null || !visit.getAssignedWorker().getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Access denied: You are not assigned to this visit.");
        }

        visit.setCompletedTime(LocalDateTime.now());
        visit.setServiceNotes(notes);
        visit.setPhotosJson(photosJson);
        visit.setStatus(ServiceVisit.VisitStatus.COMPLETED);

        // Advance subscription nextServiceDate based on plan frequency
        Subscription sub = visit.getSubscription();
        if (sub != null && sub.getMaintenancePlan() != null) {
            int daysToAdd = switch (sub.getMaintenancePlan().getFrequency()) {
                case EVERY_4_DAYS -> 4;
                case WEEKLY -> 7;
                case TWICE_MONTHLY -> 15;
                case MONTHLY, CUSTOM -> 30;
            };
            sub.setNextServiceDate(LocalDate.now().plusDays(daysToAdd));
            subscriptionRepository.save(sub);
        }

        ServiceVisit saved = visitRepository.save(visit);
        return mapToVisitResponse(saved);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    @Override
    public void generateUpcomingVisits() {
        LocalDate today = LocalDate.now();
        List<Subscription> dueSubscriptions = subscriptionRepository.findByStatusAndNextServiceDateLessThanEqual(
                Subscription.SubscriptionStatus.ACTIVE, today.plusDays(3));

        for (Subscription sub : dueSubscriptions) {
            ServiceVisit visit = new ServiceVisit();
            visit.setSubscription(sub);
            visit.setCompany(sub.getCompany());
            visit.setScheduledDate(sub.getNextServiceDate());
            visit.setStatus(ServiceVisit.VisitStatus.SCHEDULED);
            visitRepository.save(visit);
        }
    }

    private MaintenanceDto.PlanResponse mapToPlanResponse(MaintenancePlan plan) {
        MaintenanceDto.PlanResponse resp = new MaintenanceDto.PlanResponse();
        resp.setId(plan.getId());
        resp.setName(plan.getName());
        resp.setDescription(plan.getDescription());
        resp.setMonthlyPrice(plan.getMonthlyPrice());
        resp.setFrequency(plan.getFrequency().name());
        resp.setIncludedServices(plan.getIncludedServices());
        resp.setReplacementSupport(plan.isReplacementSupport());
        resp.setEmergencySupport(plan.isEmergencySupport());
        resp.setConsultationSupport(plan.isConsultationSupport());
        resp.setActive(plan.isActive());
        return resp;
    }

    private MaintenanceDto.SubscriptionResponse mapToSubscriptionResponse(Subscription sub) {
        MaintenanceDto.SubscriptionResponse resp = new MaintenanceDto.SubscriptionResponse();
        resp.setId(sub.getId());
        resp.setCompanyId(sub.getCompany().getId());
        resp.setCompanyName(sub.getCompany().getName());
        resp.setMaintenancePlanId(sub.getMaintenancePlan().getId());
        resp.setMaintenancePlanName(sub.getMaintenancePlan().getName());
        if (sub.getBooking() != null) resp.setBookingId(sub.getBooking().getId());
        resp.setStartDate(sub.getStartDate());
        resp.setEndDate(sub.getEndDate());
        resp.setNextServiceDate(sub.getNextServiceDate());
        resp.setStatus(sub.getStatus().name());
        resp.setAutoRenew(sub.isAutoRenew());
        return resp;
    }

    private MaintenanceDto.ServiceVisitResponse mapToVisitResponse(ServiceVisit visit) {
        MaintenanceDto.ServiceVisitResponse resp = new MaintenanceDto.ServiceVisitResponse();
        resp.setId(visit.getId());
        if (visit.getSubscription() != null) resp.setSubscriptionId(visit.getSubscription().getId());
        if (visit.getCompany() != null) {
            resp.setCompanyId(visit.getCompany().getId());
        } else if (visit.getSubscription() != null && visit.getSubscription().getCompany() != null) {
            resp.setCompanyId(visit.getSubscription().getCompany().getId());
        }
        if (visit.getAssignedWorker() != null) {
            resp.setAssignedWorkerId(visit.getAssignedWorker().getId());
            resp.setAssignedWorkerName(visit.getAssignedWorker().getUser().getFullName());
        }
        resp.setScheduledDate(visit.getScheduledDate());
        resp.setArrivalTime(visit.getArrivalTime());
        resp.setCompletedTime(visit.getCompletedTime());
        resp.setStatus(visit.getStatus().name());
        resp.setServiceNotes(visit.getServiceNotes());
        resp.setPhotosJson(visit.getPhotosJson());
        return resp;
    }
}
