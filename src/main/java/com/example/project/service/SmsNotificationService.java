package com.example.project.service;

public interface SmsNotificationService {
    void sendSms(String phoneNumber, String message);
}
