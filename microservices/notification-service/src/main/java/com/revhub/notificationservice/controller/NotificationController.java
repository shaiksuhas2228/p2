package com.revhub.notificationservice.controller;

import com.revhub.notificationservice.dto.NotificationRequest;
import com.revhub.notificationservice.dto.NotificationResponse;
import com.revhub.notificationservice.model.UserPreference;
import com.revhub.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        return response != null ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<NotificationResponse> notifications = notificationService.getUserNotifications(userId, page, size);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to fetch notifications: " + e.getMessage()));
        }
    }

    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId, page, size);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable String notificationId) {
        boolean success = notificationService.markAsRead(notificationId);
        return success ?
            ResponseEntity.ok("Notification marked as read") :
            ResponseEntity.notFound().build();
    }

    @PutMapping("/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    @GetMapping("/{userId}/preferences")
    public ResponseEntity<UserPreference> getUserPreferences(@PathVariable Long userId) {
        UserPreference preferences = notificationService.getUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<UserPreference> updateUserPreferences(
            @PathVariable Long userId,
            @RequestBody UserPreference preferences) {
        UserPreference updated = notificationService.updateUserPreferences(userId, preferences);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}/cleanup")
    public ResponseEntity<String> cleanupOldNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int daysOld) {
        notificationService.cleanupOldNotifications(userId, daysOld);
        return ResponseEntity.ok("Old notifications cleaned up");
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable String notificationId) {
        boolean success = notificationService.deleteNotification(notificationId);
        return success ?
            ResponseEntity.ok("Notification deleted") :
            ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}