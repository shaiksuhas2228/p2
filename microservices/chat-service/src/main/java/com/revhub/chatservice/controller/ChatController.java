package com.revhub.chatservice.controller;

import com.revhub.chatservice.dto.ChatMessageRequest;
import com.revhub.chatservice.dto.ChatMessageResponse;
import com.revhub.chatservice.dto.ConversationResponse;
import com.revhub.chatservice.model.Conversation;
import com.revhub.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<Conversation>> getUserConversations(@PathVariable Long userId) {
        List<Conversation> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<List<ChatMessageResponse>> getConversationMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<ChatMessageResponse> messages = chatService.getConversationMessages(conversationId, page, size);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/messages/{conversationId}/read")
    public ResponseEntity<String> markMessagesAsRead(
            @PathVariable String conversationId,
            @RequestParam Long userId) {
        chatService.markMessagesAsRead(conversationId, userId);
        return ResponseEntity.ok("Messages marked as read");
    }

    @GetMapping("/messages/{conversationId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable String conversationId,
            @RequestParam Long userId) {
        long count = chatService.getUnreadCount(conversationId, userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<ConversationResponse>> getChatContacts(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.ok(List.of());
        }
        List<ConversationResponse> conversations = chatService.getUserConversationsWithUnreadCount(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversation/{username}")
    public ResponseEntity<List<ChatMessageResponse>> getConversationByUsername(
            @PathVariable String username,
            @RequestParam(required = false) Long currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            if (currentUserId == null) {
                return ResponseEntity.ok(List.of());
            }
            List<ChatMessageResponse> messages = chatService.getMessagesByUsername(currentUserId, username, page, size);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.out.println("Error getting conversation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/mark-read/{username}")
    public ResponseEntity<String> markAsRead(
            @PathVariable String username,
            @RequestParam(required = false) Long currentUserId) {
        try {
            if (currentUserId != null) {
                chatService.markMessagesAsReadByUsername(currentUserId, username);
            }
            return ResponseEntity.ok("Marked as read");
        } catch (Exception e) {
            System.out.println("Error marking as read: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok("Marked as read");
        }
    }

    @GetMapping("/unread-count/{username}")
    public ResponseEntity<Long> getUnreadCountByUsername(
            @PathVariable String username,
            @RequestParam Long currentUserId) {
        long count = chatService.getUnreadCountByUsername(currentUserId, username);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/conversation/{username}")
    public ResponseEntity<String> deleteConversation(
            @PathVariable String username,
            @RequestParam Long currentUserId) {
        chatService.deleteConversationByUsername(currentUserId, username);
        return ResponseEntity.ok("Conversation deleted");
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody Map<String, String> requestBody,
            @RequestParam(required = false) Long currentUserId) {
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        ChatMessageRequest request = new ChatMessageRequest();
        request.setSenderId(currentUserId);
        request.setSenderUsername(requestBody.get("senderUsername"));
        request.setReceiverUsername(requestBody.get("receiverUsername"));
        request.setContent(requestBody.get("content"));
        ChatMessageResponse message = chatService.sendMessage(request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat Service is running");
    }
}