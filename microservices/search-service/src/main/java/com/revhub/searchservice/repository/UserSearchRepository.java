package com.revhub.searchservice.repository;

import com.revhub.searchservice.document.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, String> {
    
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"username^3\", \"bio^1\"]}}")
    Page<UserDocument> findByUsernameOrBio(String query, Pageable pageable);
    
    @Query("{\"prefix\": {\"username\": \"?0\"}}")
    Page<UserDocument> findByUsernamePrefix(String prefix, Pageable pageable);
    
    @Query("{\"match\": {\"verified\": true}}")
    Page<UserDocument> findVerifiedUsers(Pageable pageable);
    
    @Query("{\"range\": {\"followersCount\": {\"gte\": ?0}}}")
    Page<UserDocument> findPopularUsers(Integer minFollowers, Pageable pageable);
}