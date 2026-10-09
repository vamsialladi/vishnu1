package com.example.project.service;

import com.example.project.dto.NotificationDto;
import java.util.List;

public interface NotificationService {
    void sendNotification(Long userId, String title, String message, String eventType, Long referenceId);
    List<NotificationDto.Response> getNotificationsForUser(String userEmail);
    List<NotificationDto.Response> getUnreadNotificationsForUser(String userEmail);
    void markAsRead(Long notificationId, String userEmail);
    void markAllAsRead(String userEmail);
}
