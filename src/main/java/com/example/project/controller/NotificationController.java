package com.example.project.controller;

import com.example.project.dto.NotificationDto;
import com.example.project.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto.Response>> getNotifications(@RequestParam String username) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(username));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto.Response>> getUnreadNotifications(@RequestParam String username) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsForUser(username));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @RequestParam String username) {
        notificationService.markAsRead(id, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam String username) {
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok().build();
    }
}
