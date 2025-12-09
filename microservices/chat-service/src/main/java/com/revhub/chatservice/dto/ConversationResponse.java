package com.revhub.chatservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ConversationResponse {
    private String id;
    private List<Long> participants;
    private List<String> participantUsernames;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private long unreadCount;

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

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
}
