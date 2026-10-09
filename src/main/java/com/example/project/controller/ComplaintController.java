package com.example.project.controller;

import com.example.project.dto.ComplaintDto;
import com.example.project.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ComplaintDto.Response> createComplaint(@Valid @RequestBody ComplaintDto.CreateRequest request,
                                                                 @RequestParam String username) {
        return new ResponseEntity<>(complaintService.createComplaint(request, username), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDto.Response> getById(@PathVariable Long id,
                                                         @RequestParam String username) {
        return ResponseEntity.ok(complaintService.getComplaintById(id, username));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ComplaintDto.Response>> getByCompany(@PathVariable Long companyId,
                                                                    @RequestParam String username) {
        return ResponseEntity.ok(complaintService.getComplaintsForCompany(companyId, username));
    }

    @GetMapping
    public ResponseEntity<List<ComplaintDto.Response>> getAll() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ComplaintDto.Response> resolveComplaint(@PathVariable Long id,
                                                                   @RequestBody ComplaintDto.ResolveRequest request,
                                                                   @RequestParam String username) {
        return ResponseEntity.ok(complaintService.resolveComplaint(id, request, username));
    }
}
