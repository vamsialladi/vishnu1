package com.example.project.serviceimpl;

import com.example.project.dto.CompanyDto;
import com.example.project.dto.CompanyLocationDto;
import com.example.project.entity.Company;
import com.example.project.entity.CompanyLocation;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.CompanyLocationRepository;
import com.example.project.repository.CompanyRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.CompanyService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyLocationRepository locationRepository;
    private final UserRepository userRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository,
                              CompanyLocationRepository locationRepository,
                              UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identifier));
    }

    private void validateOwnershipOrAdmin(Company company, User user) {
        if (user.getRole() != Role.ROLE_ADMIN && !company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You do not have permission to access or modify this company's data.");
        }
    }

    @Override
    public CompanyDto.Response registerCompany(CompanyDto.Request request, String userEmail) {
        User user = getUser(userEmail);
        Company company = new Company();
        company.setName(request.getName());
        company.setRegistrationNumber(request.getRegistrationNumber());
        company.setContactEmail(request.getContactEmail() != null ? request.getContactEmail() : user.getEmail());
        company.setContactPhone(request.getContactPhone() != null ? request.getContactPhone() : user.getPhone());
        company.setBillingAddress(request.getBillingAddress());
        company.setOwner(user);

        Company saved = companyRepository.save(company);
        return mapToCompanyResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDto.Response getCompanyById(Long id, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        validateOwnershipOrAdmin(company, user);
        return mapToCompanyResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto.Response> getCompaniesForUser(String userEmail) {
        User user = getUser(userEmail);
        List<Company> companies;
        if (user.getRole() == Role.ROLE_ADMIN) {
            companies = companyRepository.findAll();
        } else {
            companies = companyRepository.findByOwnerId(user.getId());
        }
        return companies.stream().map(this::mapToCompanyResponse).collect(Collectors.toList());
    }

    @Override
    public CompanyDto.Response updateCompany(Long id, CompanyDto.Request request, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        validateOwnershipOrAdmin(company, user);

        company.setName(request.getName());
        company.setRegistrationNumber(request.getRegistrationNumber());
        company.setContactEmail(request.getContactEmail());
        company.setContactPhone(request.getContactPhone());
        company.setBillingAddress(request.getBillingAddress());

        Company saved = companyRepository.save(company);
        return mapToCompanyResponse(saved);
    }

    @Override
    public void updateCompanyStatus(Long id, String status) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        company.setStatus(status);
        companyRepository.save(company);
    }

    @Override
    public CompanyLocationDto.Response addLocation(CompanyLocationDto.Request request, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));
        validateOwnershipOrAdmin(company, user);

        CompanyLocation location = new CompanyLocation();
        location.setCompany(company);
        location.setLocationName(request.getLocationName());
        location.setAddress(request.getAddress());
        location.setCity(request.getCity());
        location.setState(request.getState());
        location.setZipCode(request.getZipCode());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setLocationType(request.getLocationType());
        if (request.getIsIndoor() != null) {
            location.setIndoor(request.getIsIndoor());
        }

        CompanyLocation saved = locationRepository.save(location);
        return mapToLocationResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyLocationDto.Response> getLocationsForCompany(Long companyId, String userEmail) {
        User user = getUser(userEmail);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        validateOwnershipOrAdmin(company, user);

        return locationRepository.findByCompanyId(companyId).stream()
                .map(this::mapToLocationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyLocationDto.Response updateLocation(Long locationId, CompanyLocationDto.Request request, String userEmail) {
        User user = getUser(userEmail);
        CompanyLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
        validateOwnershipOrAdmin(location.getCompany(), user);

        location.setLocationName(request.getLocationName());
        location.setAddress(request.getAddress());
        location.setCity(request.getCity());
        location.setState(request.getState());
        location.setZipCode(request.getZipCode());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setLocationType(request.getLocationType());
        if (request.getIsIndoor() != null) {
            location.setIndoor(request.getIsIndoor());
        }

        CompanyLocation saved = locationRepository.save(location);
        return mapToLocationResponse(saved);
    }

    @Override
    public void deleteLocation(Long locationId, String userEmail) {
        User user = getUser(userEmail);
        CompanyLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
        validateOwnershipOrAdmin(location.getCompany(), user);
        locationRepository.delete(location);
    }

    private CompanyDto.Response mapToCompanyResponse(Company company) {
        CompanyDto.Response resp = new CompanyDto.Response();
        resp.setId(company.getId());
        resp.setName(company.getName());
        resp.setRegistrationNumber(company.getRegistrationNumber());
        resp.setContactEmail(company.getContactEmail());
        resp.setContactPhone(company.getContactPhone());
        resp.setBillingAddress(company.getBillingAddress());
        resp.setStatus(company.getStatus());
        resp.setOwnerId(company.getOwner().getId());
        resp.setCreatedAt(company.getCreatedAt());
        return resp;
    }

    private CompanyLocationDto.Response mapToLocationResponse(CompanyLocation location) {
        CompanyLocationDto.Response resp = new CompanyLocationDto.Response();
        resp.setId(location.getId());
        resp.setCompanyId(location.getCompany().getId());
        resp.setLocationName(location.getLocationName());
        resp.setAddress(location.getAddress());
        resp.setCity(location.getCity());
        resp.setState(location.getState());
        resp.setZipCode(location.getZipCode());
        resp.setLatitude(location.getLatitude() != null ? location.getLatitude().doubleValue() : null);
        resp.setLongitude(location.getLongitude() != null ? location.getLongitude().doubleValue() : null);
        resp.setLocationType(location.getLocationType());
        resp.setIndoor(location.isIndoor());
        return resp;
    }
}
