package com.example.project.service;

import com.example.project.dto.AuthRequest;
import com.example.project.dto.AuthResponse;
import com.example.project.dto.UserRequest;
import com.example.project.dto.UserResponse;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.UserRepository;
import com.example.project.serviceimpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        sampleUser = new User("johndoe", "john@example.com", "encodedPassword", "John Doe", Role.ROLE_CUSTOMER);
        sampleUser.setId(1L);
        sampleUser.setEnabled(true);

        userRequest = new UserRequest("johndoe", "john@example.com", "plainPassword", "John Doe", Role.ROLE_CUSTOMER);
    }

    @Test
    @DisplayName("Should successfully register a new user and return AuthResponse")
    void shouldRegisterUser() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        AuthResponse response = userService.register(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("johndoe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_CUSTOMER);
        assertThat(response.getMessage()).isEqualTo("Registered successfully");
    }

    @Test
    @DisplayName("Should throw exception when registering with duplicate username")
    void shouldThrowExceptionWhenUsernameExists() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(userRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    @DisplayName("Should successfully login with correct credentials")
    void shouldLoginUser() {
        when(userRepository.findByUsernameOrEmail("johndoe", "johndoe"))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);

        AuthResponse response = userService.login(new AuthRequest("johndoe", "plainPassword"));

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("johndoe");
        assertThat(response.getMessage()).isEqualTo("Login successful");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on wrong password")
    void shouldThrowOnWrongPassword() {
        when(userRepository.findByUsernameOrEmail("johndoe", "johndoe"))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(new AuthRequest("johndoe", "wrongPassword")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    @DisplayName("Should fetch user by ID")
    void shouldGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponse response = userService.getUserById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("johndoe");
    }

    @Test
    @DisplayName("Should successfully handle logout")
    void shouldLogoutUser() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);
        userService.logout("johndoe");
        verify(userRepository, times(1)).existsByUsername("johndoe");
    }

    @Test
    @DisplayName("Should generate forgot password token")
    void shouldGenerateForgotPasswordToken() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        String result = userService.forgotPassword("john@example.com");
        assertThat(result).contains("Password reset token generated");
    }

    @Test
    @DisplayName("Should successfully reset password")
    void shouldResetPassword() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        userService.resetPassword("john@example.com", "newPassword123");

        verify(userRepository, times(1)).save(sampleUser);
        assertThat(sampleUser.getPassword()).isEqualTo("newEncodedPassword");
    }

    @Test
    @DisplayName("Should get current user")
    void shouldGetCurrentUser() {
        when(userRepository.findByUsernameOrEmail("johndoe", "johndoe")).thenReturn(Optional.of(sampleUser));
        UserResponse response = userService.getCurrentUser("johndoe");
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("johndoe");
    }
}
