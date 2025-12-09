package com.revhub.searchservice.repository;

import com.revhub.searchservice.document.PostDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, String> {
    
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"content^2\", \"hashtags^3\"]}}")
    Page<PostDocument> findByContentOrHashtags(String query, Pageable pageable);
    
    @Query("{\"match\": {\"hashtags\": \"?0\"}}")
    Page<PostDocument> findByHashtag(String hashtag, Pageable pageable);
    
    @Query("{\"match\": {\"username\": \"?0\"}}")
    Page<PostDocument> findByUsername(String username, Pageable pageable);
    
    @Query("{\"range\": {\"createdAt\": {\"gte\": \"?0\"}}}")
    Page<PostDocument> findRecentPosts(String date, Pageable pageable);
    
    List<PostDocument> findByUserId(Long userId);
}