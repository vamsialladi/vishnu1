package com.example.project.service;

import com.example.project.dto.AdminReportDto;

public interface AdminReportService {
    AdminReportDto.DashboardStats getDashboardStatistics();
}
