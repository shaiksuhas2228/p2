package com.revhub.notificationservice.repository;

import com.revhub.notificationservice.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    long countByUserIdAndReadFalse(Long userId);
    
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type, Pageable pageable);
    
    @Query("{'userId': ?0, 'priority': ?1, 'read': false}")
    List<Notification> findUnreadByUserIdAndPriority(Long userId, String priority);
    
    @Query("{'pushed': false, 'createdAt': {$gte: ?0}}")
    List<Notification> findUnpushedNotifications(LocalDateTime since);
    
    void deleteByUserIdAndCreatedAtBefore(Long userId, LocalDateTime before);
}