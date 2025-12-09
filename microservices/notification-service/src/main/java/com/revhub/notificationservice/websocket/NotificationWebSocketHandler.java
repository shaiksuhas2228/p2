package com.revhub.notificationservice.websocket;

import com.revhub.notificationservice.dto.NotificationResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
public class NotificationWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/notification.subscribe")
    public void subscribeToNotifications(@Payload String userId, Principal principal) {
        // User subscribed to notifications - can be used for tracking active users
        System.out.println("User " + userId + " subscribed to notifications");
    }

    public void sendNotificationToUser(Long userId, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/queue/notifications", 
            notification
        );
    }

    public void broadcastSystemNotification(NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/system", notification);
    }
}