package com.example.project.controller;

import com.example.project.dto.AdminReportDto;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.UserRepository;
import com.example.project.service.AdminReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    private final AdminReportService adminReportService;
    private final UserRepository userRepository;

    public AdminReportController(AdminReportService adminReportService, UserRepository userRepository) {
        this.adminReportService = adminReportService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminReportDto.DashboardStats> getDashboardStats(
            @RequestParam(required = false) String username) {
        if (username != null && !username.isBlank()) {
            User user = userRepository.findByUsernameOrEmail(username, username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
            if (user.getRole() != Role.ROLE_ADMIN) {
                throw new AccessDeniedException("Access denied: Admin role required");
            }
        }
        return ResponseEntity.ok(adminReportService.getDashboardStatistics());
    }
}
