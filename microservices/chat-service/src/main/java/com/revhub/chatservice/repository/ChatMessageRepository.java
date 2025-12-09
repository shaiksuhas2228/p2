package com.revhub.chatservice.repository;

import com.revhub.chatservice.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    List<ChatMessage> findByConversationIdOrderByTimestampDesc(String conversationId, Pageable pageable);
    
    @Query("{'conversationId': ?0, 'receiverId': ?1, 'read': false}")
    List<ChatMessage> findUnreadMessages(String conversationId, Long receiverId);
    
    long countByConversationIdAndReceiverIdAndReadFalse(String conversationId, Long receiverId);
    
    @Query("{'$or': [{'senderId': ?0}, {'receiverId': ?0}]}")
    List<ChatMessage> findUserMessages(Long userId, Pageable pageable);
    
    void deleteByConversationId(String conversationId);
}