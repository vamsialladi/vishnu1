package com.example.project.service;

import com.example.project.dto.NotificationDto;
import com.example.project.entity.Notification;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.NotificationRepository;
import com.example.project.repository.UserRepository;
import com.example.project.serviceimpl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User("user1", "user@example.com", "pass", "User One", Role.ROLE_CUSTOMER);
        user.setId(5L);

        notification = new Notification(user, "Booking Confirmed", "Your booking has been confirmed", "BOOKING_CONFIRMED", 10L);
        notification.setId(100L);
    }

    @Test
    @DisplayName("Should send in-app notification successfully")
    void shouldSendNotification() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        notificationService.sendNotification(5L, "Booking Confirmed", "Your booking has been confirmed", "BOOKING_CONFIRMED", 10L);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should fetch notifications for user")
    void shouldGetNotifications() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(notification));

        List<NotificationDto.Response> result = notificationService.getNotificationsForUser("user@example.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Booking Confirmed");
    }

    @Test
    @DisplayName("Should mark notification as read")
    void shouldMarkAsRead() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(100L, "user@example.com");

        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository, times(1)).save(notification);
    }
}
