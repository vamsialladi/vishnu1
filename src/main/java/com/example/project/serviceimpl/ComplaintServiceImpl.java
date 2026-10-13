package com.example.project.serviceimpl;

import com.example.project.dto.ComplaintDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.ComplaintService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CompanyRepository companyRepository;
    private final BookingRepository bookingRepository;
    private final ServiceVisitRepository serviceVisitRepository;
    private final UserRepository userRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                CompanyRepository companyRepository,
                                BookingRepository bookingRepository,
                                ServiceVisitRepository serviceVisitRepository,
                                UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.companyRepository = companyRepository;
        this.bookingRepository = bookingRepository;
        this.serviceVisitRepository = serviceVisitRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).or(() -> userRepository.findByUsername(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void validateCompanyAccess(Company company, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) return;
        if (!company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot view complaints of another company.");
        }
    }

    @Override
    public ComplaintDto.Response createComplaint(ComplaintDto.CreateRequest request, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + request.getCompanyId()));

        validateCompanyAccess(company, user);

        Complaint complaint = new Complaint();
        complaint.setCompany(company);
        complaint.setCategory(request.getCategory());
        complaint.setDescription(request.getDescription());
        complaint.setPhotoUrl(request.getPhotoUrl());

        if (request.getPriority() != null) {
            complaint.setPriority(Complaint.Priority.valueOf(request.getPriority().toUpperCase()));
        }

        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId()).orElse(null);
            complaint.setBooking(booking);
        }

        if (request.getServiceVisitId() != null) {
            ServiceVisit visit = serviceVisitRepository.findById(request.getServiceVisitId()).orElse(null);
            complaint.setServiceVisit(visit);
        }

        Complaint saved = complaintRepository.save(complaint);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintDto.Response getComplaintById(Long id, String userEmail) {
        User user = getUser(userEmail);
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));
        validateCompanyAccess(complaint.getCompany(), user);
        return mapToResponse(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto.Response> getComplaintsForCompany(Long companyId, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
        validateCompanyAccess(company, user);

        return complaintRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto.Response> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintDto.Response resolveComplaint(Long id, ComplaintDto.ResolveRequest request, String adminEmail) {
        User admin = getUser(adminEmail);
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));

        complaint.setAssignedAdmin(admin);
        if (request.getStatus() != null) {
            Complaint.Status status = Complaint.Status.valueOf(request.getStatus().toUpperCase());
            complaint.setStatus(status);
            if (status == Complaint.Status.RESOLVED || status == Complaint.Status.REJECTED) {
                complaint.setResolvedAt(LocalDateTime.now());
            }
        }
        if (request.getInvestigationNotes() != null) {
            complaint.setInvestigationNotes(request.getInvestigationNotes());
        }
        if (request.getResolutionNotes() != null) {
            complaint.setResolutionNotes(request.getResolutionNotes());
        }

        Complaint saved = complaintRepository.save(complaint);
        return mapToResponse(saved);
    }

    private ComplaintDto.Response mapToResponse(Complaint c) {
        ComplaintDto.Response resp = new ComplaintDto.Response();
        resp.setId(c.getId());
        resp.setCompanyId(c.getCompany().getId());
        resp.setCompanyName(c.getCompany().getName());
        resp.setCategory(c.getCategory());
        resp.setDescription(c.getDescription());
        if (c.getBooking() != null) resp.setBookingId(c.getBooking().getId());
        if (c.getServiceVisit() != null) resp.setServiceVisitId(c.getServiceVisit().getId());
        resp.setPhotoUrl(c.getPhotoUrl());
        resp.setPriority(c.getPriority().name());
        resp.setStatus(c.getStatus().name());
        if (c.getAssignedAdmin() != null) {
            resp.setAssignedAdminId(c.getAssignedAdmin().getId());
            resp.setAssignedAdminName(c.getAssignedAdmin().getFullName());
        }
        resp.setInvestigationNotes(c.getInvestigationNotes());
        resp.setResolutionNotes(c.getResolutionNotes());
        resp.setCreatedAt(c.getCreatedAt());
        resp.setResolvedAt(c.getResolvedAt());
        return resp;
    }
}
