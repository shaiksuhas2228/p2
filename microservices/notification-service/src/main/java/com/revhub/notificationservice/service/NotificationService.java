package com.revhub.notificationservice.service;

import com.revhub.notificationservice.dto.NotificationRequest;
import com.revhub.notificationservice.dto.NotificationResponse;
import com.revhub.notificationservice.model.Notification;
import com.revhub.notificationservice.model.UserPreference;
import com.revhub.notificationservice.repository.NotificationRepository;
import com.revhub.notificationservice.repository.UserPreferenceRepository;
import com.revhub.notificationservice.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private NotificationWebSocketHandler webSocketHandler;

    public NotificationResponse createNotification(NotificationRequest request) {
        // Check user preferences
        UserPreference preferences = getUserPreferences(request.getUserId());
        if (!shouldSendNotification(preferences, request.getType())) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setPriority(request.getPriority());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        notification.setFollowRequestId(request.getFollowRequestId());

        Notification saved = notificationRepository.save(notification);

        // Send real-time push notification
        if (preferences.isPushEnabled()) {
            pushNotification(saved);
        }

        return convertToResponse(saved);
    }

    public List<NotificationResponse> getUserNotifications(Long userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<NotificationResponse> getUnreadNotifications(Long userId, int page, int size) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public boolean markAsRead(String notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            notification.get().setRead(true);
            notificationRepository.save(notification.get());
            return true;
        }
        return false;
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId, PageRequest.of(0, 1000));
        
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public UserPreference updateUserPreferences(Long userId, UserPreference preferences) {
        preferences.setUserId(userId);
        return userPreferenceRepository.save(preferences);
    }

    public UserPreference getUserPreferences(Long userId) {
        return userPreferenceRepository.findFirstByUserId(userId)
                .orElse(createDefaultPreferences(userId));
    }

    public void cleanupOldNotifications(Long userId, int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        notificationRepository.deleteByUserIdAndCreatedAtBefore(userId, cutoff);
    }

    public boolean deleteNotification(String notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }

    private void pushNotification(Notification notification) {
        try {
            NotificationResponse response = convertToResponse(notification);
            
            // Send via WebSocket
            if ("SYSTEM".equals(notification.getType())) {
                webSocketHandler.broadcastSystemNotification(response);
            } else {
                webSocketHandler.sendNotificationToUser(notification.getUserId(), response);
            }
            
            // Update MongoDB
            notification.setPushed(true);
            notificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("Failed to push notification: " + e.getMessage());
        }
    }

    private boolean shouldSendNotification(UserPreference preferences, String type) {
        return preferences.getTypePreferences().getOrDefault(type, true);
    }

    private UserPreference createDefaultPreferences(Long userId) {
        UserPreference preferences = new UserPreference();
        preferences.setUserId(userId);
        return userPreferenceRepository.save(preferences);
    }

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUserId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setPriority(notification.getPriority());
        response.setRead(notification.isRead());
        response.setRelatedEntityId(notification.getRelatedEntityId());
        response.setRelatedEntityType(notification.getRelatedEntityType());
        response.setFollowRequestId(notification.getFollowRequestId());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReadAt(notification.getReadAt());
        return response;
    }
}