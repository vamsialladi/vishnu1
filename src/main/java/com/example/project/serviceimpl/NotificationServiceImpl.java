package com.example.project.serviceimpl;

import com.example.project.dto.NotificationDto;
import com.example.project.entity.Notification;
import com.example.project.entity.User;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.NotificationRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).or(() -> userRepository.findByUsername(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    public void sendNotification(Long userId, String title, String message, String eventType, Long referenceId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setEventType(eventType);
        notification.setReferenceId(referenceId);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto.Response> getNotificationsForUser(String userEmail) {
        User user = getUser(userEmail);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto.Response> getUnreadNotificationsForUser(String userEmail) {
        User user = getUser(userEmail);
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId, String userEmail) {
        User user = getUser(userEmail);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot modify another user's notification.");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String userEmail) {
        User user = getUser(userEmail);
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    private NotificationDto.Response mapToResponse(Notification n) {
        NotificationDto.Response resp = new NotificationDto.Response();
        resp.setId(n.getId());
        resp.setUser(n.getUser().getId());
        resp.setTitle(n.getTitle());
        resp.setMessage(n.getMessage());
        resp.setEventType(n.getEventType());
        resp.setReferenceId(n.getReferenceId());
        resp.setRead(n.isRead());
        resp.setCreatedAt(n.getCreatedAt());
        return resp;
    }
}
