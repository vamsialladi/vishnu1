package com.example.project.service;

public interface EmailNotificationService {
    void sendEmail(String recipientEmail, String subject, String body);
}
