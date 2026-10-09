package com.example.project.serviceimpl;

import com.example.project.service.EmailNotificationService;
import com.example.project.service.PushNotificationService;
import com.example.project.service.SmsNotificationService;
import com.example.project.service.WhatsAppNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockExternalNotificationServices implements EmailNotificationService,
        SmsNotificationService, WhatsAppNotificationService, PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(MockExternalNotificationServices.class);

    @Override
    public void sendEmail(String recipientEmail, String subject, String body) {
        log.info("[EMAIL] Sending to: {}, Subject: {}", recipientEmail, subject);
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("[SMS] Sending to: {}, Message: {}", phoneNumber, message);
    }

    @Override
    public void sendWhatsApp(String phoneNumber, String message) {
        log.info("[WHATSAPP] Sending to: {}, Message: {}", phoneNumber, message);
    }

    @Override
    public void sendPushNotification(String userToken, String title, String message) {
        log.info("[PUSH] Sending to: {}, Title: {}, Message: {}", userToken, title, message);
    }
}
