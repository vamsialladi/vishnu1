package com.example.project.controller;

import com.example.project.entity.Company;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.CompanyRepository;
import com.example.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityAndIdorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private User customer1;
    private User customer2;
    private User worker;
    private User admin;
    private Company company1;

    @BeforeEach
    void setUp() {
        customer1 = new User("cust1", "cust1@example.com", "pass123", "Customer 1", Role.ROLE_CUSTOMER);
        customer1.setEnabled(true);
        customer1 = userRepository.save(customer1);

        customer2 = new User("cust2", "cust2@example.com", "pass123", "Customer 2", Role.ROLE_CUSTOMER);
        customer2.setEnabled(true);
        customer2 = userRepository.save(customer2);

        worker = new User("worker1", "worker1@example.com", "pass123", "Worker 1", Role.ROLE_WORKER);
        worker.setEnabled(true);
        worker = userRepository.save(worker);

        admin = new User("admin1", "admin1@example.com", "pass123", "Admin 1", Role.ROLE_ADMIN);
        admin.setEnabled(true);
        admin = userRepository.save(admin);

        company1 = new Company("Company 1", "REG-C1", "cust1@example.com", customer1);
        company1 = companyRepository.save(company1);
    }

    @Test
    @DisplayName("IDOR Prevention: Customer 2 cannot access Customer 1 company data (403 Forbidden)")
    void shouldPreventCustomerAccessingOtherCompany() throws Exception {
        mockMvc.perform(get("/api/v1/companies/" + company1.getId())
                        .param("username", "cust2@example.com"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Access denied")));
    }

    @Test
    @DisplayName("Ownership Validation: Customer 1 can access own company data (200 OK)")
    void shouldAllowCustomerAccessingOwnCompany() throws Exception {
        mockMvc.perform(get("/api/v1/companies/" + company1.getId())
                        .param("username", "cust1@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(company1.getId()));
    }

    @Test
    @DisplayName("Role Authorization: Customer cannot call Admin report API (403 Forbidden)")
    void shouldPreventCustomerCallingAdminApi() throws Exception {
        mockMvc.perform(get("/api/admin/reports/dashboard")
                        .param("username", "cust1@example.com"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Admin role required")));
    }

    @Test
    @DisplayName("Role Authorization: Worker cannot call Admin report API (403 Forbidden)")
    void shouldPreventWorkerCallingAdminApi() throws Exception {
        mockMvc.perform(get("/api/admin/reports/dashboard")
                        .param("username", "worker1@example.com"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Admin role required")));
    }

    @Test
    @DisplayName("Role Authorization: Admin can successfully call Admin report API (200 OK)")
    void shouldAllowAdminCallingAdminApi() throws Exception {
        mockMvc.perform(get("/api/admin/reports/dashboard")
                        .param("username", "admin1@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCustomers").isNumber());
    }

    @Test
    @DisplayName("API Error Handling: Requesting non-existent company returns 404 Not Found")
    void shouldReturn404ForNonExistentResource() throws Exception {
        mockMvc.perform(get("/api/v1/companies/999999")
                        .param("username", "admin1@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("API Error Handling: Validation error returns 400 Bad Request")
    void shouldReturn400OnValidationFailure() throws Exception {
        // Empty registration payload
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Correlation ID: Responses should contain X-Correlation-ID header")
    void shouldContainCorrelationIdHeader() throws Exception {
        mockMvc.perform(get("/api/v1/plants")
                        .header("X-Correlation-ID", "test-corr-12345"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("X-Correlation-ID", "test-corr-12345"));
    }
}
