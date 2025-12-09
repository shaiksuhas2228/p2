package com.revhub.postservice.repository;

import com.revhub.postservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h.tagName = :tagName ORDER BY p.createdAt DESC")
    List<Post> findByHashtagOrderByCreatedAtDesc(@Param("tagName") String tagName);
    
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderByCreatedAtDesc();
    
    List<Post> findByContentContainingIgnoreCase(String content);
}