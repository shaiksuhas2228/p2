package com.revhub.notificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private Long userId;
    private String title;
    private String message;
    private String type; // FOLLOW, LIKE, COMMENT, POST, SYSTEM
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private boolean read = false;
    private boolean pushed = false;
    private Long relatedEntityId;
    private String relatedEntityType;
    private Long actorId; // User who triggered the notification
    private String actorUsername;
    private String actorProfilePicture;
    private Long followRequestId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { 
        this.read = read;
        if (read && readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    public boolean isPushed() { return pushed; }
    public void setPushed(boolean pushed) { this.pushed = pushed; }

    public Long getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getActorUsername() { return actorUsername; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }

    public String getActorProfilePicture() { return actorProfilePicture; }
    public void setActorProfilePicture(String actorProfilePicture) { this.actorProfilePicture = actorProfilePicture; }

    public Long getFollowRequestId() { return followRequestId; }
    public void setFollowRequestId(Long followRequestId) { this.followRequestId = followRequestId; }
}