package com.example.project.service;

import com.example.project.dto.CompanyDto;
import com.example.project.entity.Company;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.CompanyLocationRepository;
import com.example.project.repository.CompanyRepository;
import com.example.project.repository.UserRepository;
import com.example.project.serviceimpl.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyLocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private User testUser;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        testUser = new User("corpuser", "corp@company.com", "secret", "Corp Admin", Role.ROLE_CUSTOMER);
        testUser.setId(10L);

        testCompany = new Company("Green Office Corp", "REG-12345", "corp@company.com", "9876543210", testUser);
        testCompany.setId(100L);
    }

    @Test
    void testRegisterCompanySuccess() {
        CompanyDto.Request request = new CompanyDto.Request();
        request.setName("Green Office Corp");
        request.setRegistrationNumber("REG-12345");
        request.setContactEmail("corp@company.com");
        request.setContactPhone("9876543210");
        request.setBillingAddress("123 Tech Park");

        when(userRepository.findByEmail("corp@company.com")).thenReturn(Optional.of(testUser));
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        CompanyDto.Response response = companyService.registerCompany(request, "corp@company.com");

        assertNotNull(response);
        assertEquals("Green Office Corp", response.getName());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void testGetCompanyByIdSuccess() {
        when(userRepository.findByEmail("corp@company.com")).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(100L)).thenReturn(Optional.of(testCompany));

        CompanyDto.Response response = companyService.getCompanyById(100L, "corp@company.com");

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Green Office Corp", response.getName());
    }
}
