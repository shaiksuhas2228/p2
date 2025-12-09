package com.revhub.notificationservice.controller;

import com.revhub.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
public class NotificationWebSocketController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notifications.subscribe")
    public void subscribeToNotifications(@Payload String message, Principal principal) {
        // Send unread count when user connects
        try {
            Long userId = Long.parseLong(message);
            long unreadCount = notificationService.getUnreadCount(userId);
            
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/unread-count",
                unreadCount
            );
        } catch (Exception e) {
            System.err.println("Error handling notification subscription: " + e.getMessage());
        }
    }

    @MessageMapping("/notifications.markRead")
    public void markNotificationAsRead(@Payload String notificationId, Principal principal) {
        try {
            boolean success = notificationService.markAsRead(notificationId);
            
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/read-status",
                success ? "READ" : "ERROR"
            );
        } catch (Exception e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
        }
    }
}