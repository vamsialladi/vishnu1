package com.example.project.service;

import com.example.project.dto.AuthRequest;
import com.example.project.dto.AuthResponse;
import com.example.project.dto.UserRequest;
import com.example.project.dto.UserResponse;

import java.util.List;

public interface UserService {

    AuthResponse register(UserRequest userRequest);

    AuthResponse login(AuthRequest authRequest);

    void logout(String username);

    String forgotPassword(String email);

    void resetPassword(String tokenOrEmail, String newPassword);

    UserResponse getCurrentUser(String username);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);
}
