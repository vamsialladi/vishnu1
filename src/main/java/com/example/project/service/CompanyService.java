package com.example.project.service;

import com.example.project.dto.CompanyDto;
import com.example.project.dto.CompanyLocationDto;

import java.util.List;

public interface CompanyService {
    CompanyDto.Response registerCompany(CompanyDto.Request request, String userEmail);
    CompanyDto.Response getCompanyById(Long id, String userEmail);
    List<CompanyDto.Response> getCompaniesForUser(String userEmail);
    CompanyDto.Response updateCompany(Long id, CompanyDto.Request request, String userEmail);
    void updateCompanyStatus(Long id, String status);

    CompanyLocationDto.Response addLocation(CompanyLocationDto.Request request, String userEmail);
    List<CompanyLocationDto.Response> getLocationsForCompany(Long companyId, String userEmail);
    CompanyLocationDto.Response updateLocation(Long locationId, CompanyLocationDto.Request request, String userEmail);
    void deleteLocation(Long locationId, String userEmail);
}
