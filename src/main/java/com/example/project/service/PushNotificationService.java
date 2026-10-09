package com.example.project.service;

public interface PushNotificationService {
    void sendPushNotification(String userToken, String title, String message);
}
