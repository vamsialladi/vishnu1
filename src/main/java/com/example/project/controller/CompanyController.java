package com.example.project.controller;

import com.example.project.dto.CompanyDto;
import com.example.project.dto.CompanyLocationDto;
import com.example.project.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * All endpoints accept ?username= query param to identify the caller.
     */

    @PostMapping
    public ResponseEntity<CompanyDto.Response> registerCompany(@Valid @RequestBody CompanyDto.Request request,
                                                               @RequestParam String username) {
        return new ResponseEntity<>(companyService.registerCompany(request, username), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto.Response> getCompanyById(@PathVariable Long id,
                                                              @RequestParam String username) {
        return ResponseEntity.ok(companyService.getCompanyById(id, username));
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto.Response>> getCompanies(@RequestParam String username) {
        return ResponseEntity.ok(companyService.getCompaniesForUser(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto.Response> updateCompany(@PathVariable Long id,
                                                             @Valid @RequestBody CompanyDto.Request request,
                                                             @RequestParam String username) {
        return ResponseEntity.ok(companyService.updateCompany(id, request, username));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        companyService.updateCompanyStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    // Location endpoints
    @PostMapping("/locations")
    public ResponseEntity<CompanyLocationDto.Response> addLocation(@Valid @RequestBody CompanyLocationDto.Request request,
                                                                   @RequestParam String username) {
        return new ResponseEntity<>(companyService.addLocation(request, username), HttpStatus.CREATED);
    }

    @GetMapping("/{companyId}/locations")
    public ResponseEntity<List<CompanyLocationDto.Response>> getLocations(@PathVariable Long companyId,
                                                                          @RequestParam String username) {
        return ResponseEntity.ok(companyService.getLocationsForCompany(companyId, username));
    }

    @PutMapping("/locations/{locationId}")
    public ResponseEntity<CompanyLocationDto.Response> updateLocation(@PathVariable Long locationId,
                                                                      @Valid @RequestBody CompanyLocationDto.Request request,
                                                                      @RequestParam String username) {
        return ResponseEntity.ok(companyService.updateLocation(locationId, request, username));
    }

    @DeleteMapping("/locations/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long locationId,
                                               @RequestParam String username) {
        companyService.deleteLocation(locationId, username);
        return ResponseEntity.noContent().build();
    }
}
