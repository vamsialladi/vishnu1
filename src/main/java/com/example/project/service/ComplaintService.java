package com.example.project.service;

import com.example.project.dto.ComplaintDto;
import java.util.List;

public interface ComplaintService {
    ComplaintDto.Response createComplaint(ComplaintDto.CreateRequest request, String userEmail);
    ComplaintDto.Response getComplaintById(Long id, String userEmail);
    List<ComplaintDto.Response> getComplaintsForCompany(Long companyId, String userEmail);
    List<ComplaintDto.Response> getAllComplaints();
    ComplaintDto.Response resolveComplaint(Long id, ComplaintDto.ResolveRequest request, String adminEmail);
}
