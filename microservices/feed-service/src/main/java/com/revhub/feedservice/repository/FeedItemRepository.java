package com.revhub.feedservice.repository;

import com.revhub.feedservice.entity.FeedItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedItemRepository extends JpaRepository<FeedItem, Long> {
    
    @Query("SELECT f FROM FeedItem f WHERE f.userId = :userId ORDER BY f.score DESC, f.createdAt DESC")
    List<FeedItem> findPersonalizedFeed(@Param("userId") Long userId, Pageable pageable);
    
    void deleteByUserIdAndPostId(Long userId, Long postId);
    
    boolean existsByUserIdAndPostId(Long userId, Long postId);
}