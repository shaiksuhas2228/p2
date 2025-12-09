package com.revhub.chatservice.service;

import com.revhub.chatservice.dto.ChatMessageRequest;
import com.revhub.chatservice.dto.ChatMessageResponse;
import com.revhub.chatservice.dto.ConversationResponse;
import com.revhub.chatservice.model.ChatMessage;
import com.revhub.chatservice.model.Conversation;
import com.revhub.chatservice.repository.ChatMessageRepository;
import com.revhub.chatservice.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        // Get receiver ID from user-service
        Long receiverId = request.getReceiverId();
        if (receiverId == null || receiverId == 0) {
            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                String url = "http://localhost:8081/api/users/" + request.getReceiverUsername();
                java.util.Map<String, Object> user = restTemplate.getForObject(url, java.util.Map.class);
                if (user != null && user.get("id") != null) {
                    receiverId = Long.valueOf(user.get("id").toString());
                    request.setReceiverId(receiverId);
                }
            } catch (Exception e) {
                System.out.println("Failed to get receiver ID: " + e.getMessage());
            }
        }

        // Get or create conversation
        String conversationId = getOrCreateConversation(
            request.getSenderId(), receiverId,
            request.getSenderUsername(), request.getReceiverUsername()
        );

        // Create message
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setSenderId(request.getSenderId());
        message.setSenderUsername(request.getSenderUsername());
        message.setReceiverId(receiverId);
        message.setReceiverUsername(request.getReceiverUsername());
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Update conversation
        updateConversation(conversationId, request.getContent());
        
        // Send notification
        if (receiverId != null && receiverId > 0) {
            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                java.util.Map<String, Object> notification = new java.util.HashMap<>();
                notification.put("userId", receiverId);
                notification.put("title", "New Message");
                notification.put("message", request.getSenderUsername() + " sent you a message");
                notification.put("type", "MESSAGE");
                notification.put("priority", "HIGH");
                notification.put("relatedEntityId", request.getSenderId());
                notification.put("relatedEntityType", "MESSAGE");
                restTemplate.postForObject("http://localhost:8085/api/notifications", notification, java.util.Map.class);
            } catch (Exception e) {
                System.out.println("Failed to send notification: " + e.getMessage());
            }
        }

        return convertToResponse(savedMessage);
    }

    public List<ChatMessageResponse> getConversationMessages(String conversationId, int page, int size) {
        return chatMessageRepository.findByConversationIdOrderByTimestampDesc(
                conversationId, PageRequest.of(page, size))
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByParticipantsContaining(userId);
    }

    public List<ConversationResponse> getUserConversationsWithUnreadCount(Long userId) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContaining(userId);
        return conversations.stream()
                .map(conv -> {
                    ConversationResponse response = new ConversationResponse();
                    response.setId(conv.getId());
                    response.setParticipants(conv.getParticipants());
                    response.setParticipantUsernames(conv.getParticipantUsernames());
                    response.setLastMessage(conv.getLastMessage());
                    response.setLastMessageTime(conv.getLastMessageTime());
                    response.setUnreadCount(getUnreadCount(conv.getId(), userId));
                    return response;
                })
                .toList();
    }

    public void markMessagesAsRead(String conversationId, Long userId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessages(conversationId, userId);
        unreadMessages.forEach(message -> {
            message.setRead(true);
            chatMessageRepository.save(message);
        });
    }

    public long getUnreadCount(String conversationId, Long userId) {
        return chatMessageRepository.countByConversationIdAndReceiverIdAndReadFalse(conversationId, userId);
    }

    private String getOrCreateConversation(Long senderId, Long receiverId, String senderUsername, String receiverUsername) {
        Optional<Conversation> existing = conversationRepository.findByParticipants(senderId, receiverId);
        
        if (existing.isPresent()) {
            return existing.get().getId();
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setParticipants(Arrays.asList(senderId, receiverId));
        conversation.setParticipantUsernames(Arrays.asList(senderUsername, receiverUsername));
        
        Conversation saved = conversationRepository.save(conversation);
        return saved.getId();
    }

    private void updateConversation(String conversationId, String lastMessage) {
        conversationRepository.findById(conversationId).ifPresent(conversation -> {
            conversation.setLastMessage(lastMessage);
            conversation.setLastMessageTime(java.time.LocalDateTime.now());
            conversationRepository.save(conversation);
        });
    }

    public List<ChatMessageResponse> getMessagesByUsername(Long currentUserId, String otherUsername, int page, int size) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContainingAndParticipantUsernamesContaining(currentUserId, otherUsername);
        if (conversations.isEmpty()) {
            return List.of();
        }
        return getConversationMessages(conversations.get(0).getId(), page, size);
    }

    public void markMessagesAsReadByUsername(Long currentUserId, String otherUsername) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContainingAndParticipantUsernamesContaining(currentUserId, otherUsername);
        if (!conversations.isEmpty()) {
            markMessagesAsRead(conversations.get(0).getId(), currentUserId);
        }
    }

    public long getUnreadCountByUsername(Long currentUserId, String otherUsername) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContainingAndParticipantUsernamesContaining(currentUserId, otherUsername);
        return conversations.isEmpty() ? 0L : getUnreadCount(conversations.get(0).getId(), currentUserId);
    }

    public void deleteConversationByUsername(Long currentUserId, String otherUsername) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContainingAndParticipantUsernamesContaining(currentUserId, otherUsername);
        if (!conversations.isEmpty()) {
            String conversationId = conversations.get(0).getId();
            chatMessageRepository.deleteByConversationId(conversationId);
            conversationRepository.deleteById(conversationId);
        }
    }

    private ChatMessageResponse convertToResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversationId());
        response.setSenderId(message.getSenderId());
        response.setSenderUsername(message.getSenderUsername());
        response.setReceiverId(message.getReceiverId());
        response.setReceiverUsername(message.getReceiverUsername());
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType());
        response.setRead(message.isRead());
        response.setTimestamp(message.getTimestamp());
        return response;
    }
}