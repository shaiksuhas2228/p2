package com.revhub.feedservice.controller;

import com.revhub.feedservice.dto.FeedResponse;
import com.revhub.feedservice.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FeedResponse>> getPersonalizedFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<FeedResponse> feed = feedService.getPersonalizedFeed(userId, page, size);
        return ResponseEntity.ok(feed);
    }

    @PostMapping("/follow")
    public ResponseEntity<String> followUser(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        
        feedService.followUser(followerId, followingId);
        return ResponseEntity.ok("User followed successfully");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Feed Service is running");
    }
}