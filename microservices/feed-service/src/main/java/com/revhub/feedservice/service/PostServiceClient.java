package com.revhub.feedservice.service;

import com.revhub.feedservice.dto.FeedResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class PostServiceClient {

    @Value("${post-service.url:http://localhost:8082}")
    private String postServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public FeedResponse getPostDetails(Long postId) {
        try {
            String url = postServiceUrl + "/api/posts/" + postId;
            return restTemplate.getForObject(url, FeedResponse.class);
        } catch (Exception e) {
            System.err.println("Error fetching post details: " + e.getMessage());
            return null;
        }
    }

    public List<FeedResponse> getRecentPostsByUser(Long userId, int limit) {
        try {
            String url = postServiceUrl + "/api/posts/user/" + userId + "?limit=" + limit;
            FeedResponse[] posts = restTemplate.getForObject(url, FeedResponse[].class);
            return posts != null ? Arrays.asList(posts) : List.of();
        } catch (Exception e) {
            System.err.println("Error fetching user posts: " + e.getMessage());
            return List.of();
        }
    }
}