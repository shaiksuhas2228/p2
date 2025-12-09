package com.revhub.chatservice.repository;

import com.revhub.chatservice.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    
    @Query("{'participants': {'$all': [?0, ?1]}}")
    Optional<Conversation> findByParticipants(Long userId1, Long userId2);
    
    @Query("{'participants': ?0}")
    List<Conversation> findByParticipantsContaining(Long userId);
    
    @Query("{'participants': ?0, 'participantUsernames': ?1}")
    List<Conversation> findByParticipantsContainingAndParticipantUsernamesContaining(Long userId, String username);
}