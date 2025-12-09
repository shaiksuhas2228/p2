package com.revhub.chatservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "conversations")
public class Conversation {
    @Id
    private String id;
    private List<Long> participants;
    private List<String> participantUsernames;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private LocalDateTime createdAt;

    public Conversation() {
        this.createdAt = LocalDateTime.now();
        this.lastMessageTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<Long> getParticipants() { return participants; }
    public void setParticipants(List<Long> participants) { this.participants = participants; }

    public List<String> getParticipantUsernames() { return participantUsernames; }
    public void setParticipantUsernames(List<String> participantUsernames) { this.participantUsernames = participantUsernames; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}