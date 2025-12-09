package com.revhub.chatservice.websocket;

import com.revhub.chatservice.dto.ChatMessageRequest;
import com.revhub.chatservice.dto.ChatMessageResponse;
import com.revhub.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
public class ChatWebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        try {
            System.out.println("Received message from " + request.getSenderUsername() + " to " + request.getReceiverUsername());
            
            // Save message to MongoDB
            ChatMessageResponse response = chatService.sendMessage(request);
            System.out.println("Message saved with ID: " + response.getId());

            // Send to receiver's queue
            messagingTemplate.convertAndSendToUser(
                request.getReceiverUsername(),
                "/queue/messages",
                response
            );
            System.out.println("Sent to receiver: " + request.getReceiverUsername());

            // Send confirmation to sender's queue
            messagingTemplate.convertAndSendToUser(
                request.getSenderUsername(),
                "/queue/messages",
                response
            );
            System.out.println("Sent to sender: " + request.getSenderUsername());
            
            // Also broadcast to topic for both users
            messagingTemplate.convertAndSend("/topic/messages/" + request.getReceiverUsername(), response);
            messagingTemplate.convertAndSend("/topic/messages/" + request.getSenderUsername(), response);

        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat.join")
    public void joinConversation(@Payload String username, Principal principal) {
        System.out.println("User " + username + " joined chat");
    }

    @MessageMapping("/chat.typing")
    public void userTyping(@Payload ChatTypingEvent event, Principal principal) {
        // Send typing indicator to other user
        messagingTemplate.convertAndSendToUser(
            event.getReceiverUsername(),
            "/queue/typing",
            event
        );
    }

    public static class ChatTypingEvent {
        private String senderUsername;
        private String receiverUsername;
        private boolean typing;

        // Getters and Setters
        public String getSenderUsername() { return senderUsername; }
        public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

        public String getReceiverUsername() { return receiverUsername; }
        public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }

        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
    }
}