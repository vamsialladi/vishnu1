package com.example.project.dto;

import java.time.LocalDateTime;

public class NotificationDto {

    public static class Response {
        private Long id;
        private Long userId;
        private String title;
        private String message;
        private String eventType;
        private Long referenceId;
        private boolean isRead;
        private LocalDateTime createdAt;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getUserId() { return userId; }
        public void setUser(Long userId) { this.userId = userId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }

        public Long getReferenceId() { return referenceId; }
        public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
