package com.example.project.controller;

import com.example.project.dto.AuthRequest;
import com.example.project.dto.AuthResponse;
import com.example.project.dto.UserRequest;
import com.example.project.dto.UserResponse;
import com.example.project.entity.Role;
import com.example.project.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/v1/users/register should return 201 Created with user info")
    void shouldRegisterUserEndpoint() throws Exception {
        UserRequest request = new UserRequest("john", "john@example.com", "secret123", "John Doe", Role.ROLE_CUSTOMER);
        AuthResponse response = new AuthResponse(1L, "john", "john@example.com", Role.ROLE_CUSTOMER, "Registered successfully");

        when(userService.register(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.message").value("Registered successfully"));
    }

    @Test
    @DisplayName("POST /api/v1/users/login should return 200 OK with user info")
    void shouldLoginUserEndpoint() throws Exception {
        AuthRequest request = new AuthRequest("john", "secret123");
        AuthResponse response = new AuthResponse(1L, "john", "john@example.com", Role.ROLE_CUSTOMER, "Login successful");

        when(userService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} should return UserResponse")
    void shouldGetUserById() throws Exception {
        UserResponse response = new UserResponse(1L, "john", "john@example.com", "John Doe",
                "1234567890", Role.ROLE_CUSTOMER, true, LocalDateTime.now(), LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/users/logout should return 200 OK")
    void shouldLogoutEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/users/logout")
                        .param("username", "john"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/users/me should return current user")
    void shouldGetCurrentUserEndpoint() throws Exception {
        UserResponse response = new UserResponse(1L, "john", "john@example.com", "John Doe",
                "1234567890", Role.ROLE_CUSTOMER, true, LocalDateTime.now(), LocalDateTime.now());

        when(userService.getCurrentUser("john")).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me")
                        .param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }
}
