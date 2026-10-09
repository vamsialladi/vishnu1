package com.example.project.controller;

import com.example.project.dto.CompanyDto;
import com.example.project.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    private CompanyDto.Response sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new CompanyDto.Response();
        sampleResponse.setId(1L);
        sampleResponse.setName("EcoOffice Green Solutions");
        sampleResponse.setRegistrationNumber("REG-555");
        sampleResponse.setContactEmail("info@ecooffice.com");
        sampleResponse.setStatus("ACTIVE");
    }

    @Test
    void testGetCompanies() throws Exception {
        when(companyService.getCompaniesForUser("corp@test.com")).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/companies").param("username", "corp@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("EcoOffice Green Solutions"));
    }

    @Test
    void testRegisterCompany() throws Exception {
        CompanyDto.Request req = new CompanyDto.Request();
        req.setName("EcoOffice Green Solutions");
        req.setRegistrationNumber("REG-555");
        req.setContactEmail("info@ecooffice.com");

        when(companyService.registerCompany(any(CompanyDto.Request.class), eq("corp@test.com"))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/companies")
                        .param("username", "corp@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("EcoOffice Green Solutions"));
    }
}
