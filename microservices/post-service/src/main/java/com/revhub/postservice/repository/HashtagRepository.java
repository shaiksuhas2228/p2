package com.revhub.postservice.repository;

import com.revhub.postservice.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findByPostId(Long postId);
    
    @Query("SELECT h.tagName, COUNT(h) as count FROM Hashtag h GROUP BY h.tagName ORDER BY count DESC")
    List<Object[]> findTrendingHashtags();
}