package com.revhub.followservice.controller;

import com.revhub.followservice.dto.*;
import com.revhub.followservice.service.FollowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestBody java.util.Map<String, Object> request) {
        if (request == null || !request.containsKey("followerId") || !request.containsKey("followingUsername")) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "Invalid request"));
        }
        
        try {
            Long followerId = ((Number) request.get("followerId")).longValue();
            String followingUsername = (String) request.get("followingUsername");
            
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> user = restTemplate.getForObject(
                "http://localhost:8081/api/users/" + followingUsername, 
                java.util.Map.class
            );
            
            if (user == null || !user.containsKey("id")) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
            }
            
            Long followingId = ((Number) user.get("id")).longValue();
            
            if (followService.isFollowing(followerId, followingId)) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "Already following this user"));
            }
            
            FollowRequest followRequest = new FollowRequest();
            followRequest.setFollowerId(followerId);
            followRequest.setFollowingId(followingId);
            
            followService.followUser(followRequest);
            return ResponseEntity.ok(java.util.Map.of("message", "User followed successfully"));
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Internal server error"));
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestBody java.util.Map<String, Object> request) {
        if (request == null || !request.containsKey("followerId") || !request.containsKey("followingUsername")) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "Invalid request"));
        }
        
        try {
            Long followerId = ((Number) request.get("followerId")).longValue();
            String followingUsername = (String) request.get("followingUsername");
            
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> user = restTemplate.getForObject(
                "http://localhost:8081/api/users/" + followingUsername, 
                java.util.Map.class
            );
            
            if (user == null || !user.containsKey("id")) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
            }
            
            Long followingId = ((Number) user.get("id")).longValue();
            
            FollowRequest followRequest = new FollowRequest();
            followRequest.setFollowerId(followerId);
            followRequest.setFollowingId(followingId);
            
            followService.unfollowUser(followRequest);
            return ResponseEntity.ok(java.util.Map.of("message", "User unfollowed successfully"));
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Internal server error"));
        }
    }
    
    @GetMapping("/status/{username}")
    public ResponseEntity<?> getFollowStatus(@PathVariable String username) {
        try {
            // Get current user from request header or session
            // For now, we'll check if any follow relationship exists
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> user = restTemplate.getForObject(
                "http://localhost:8081/api/users/" + username, 
                java.util.Map.class
            );
            Long followingId = ((Number) user.get("id")).longValue();
            
            // Check all follows to see if this user is followed
            // This is a simplified check - in production you'd pass the current user ID
            String status = "NOT_FOLLOWING";
            return ResponseEntity.ok(java.util.Map.of("status", status));
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Map.of("status", "NOT_FOLLOWING"));
        }
    }

    @GetMapping("/is-following")
    public ResponseEntity<Boolean> isFollowing(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        boolean isFollowing = followService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/{userIdOrUsername}/followers")
    public ResponseEntity<?> getFollowers(
            @PathVariable String userIdOrUsername,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId;
            try {
                userId = Long.parseLong(userIdOrUsername);
            } catch (NumberFormatException e) {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                java.util.Map<String, Object> user = restTemplate.getForObject("http://localhost:8081/api/users/" + userIdOrUsername, java.util.Map.class);
                if (user == null || !user.containsKey("id")) {
                    return ResponseEntity.ok(new java.util.ArrayList<>());
                }
                userId = ((Number) user.get("id")).longValue();
            }
            
            List<Long> followerIds = followService.getFollowers(userId, page, size);
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.List<java.util.Map<String, Object>> users = new java.util.ArrayList<>();
            
            for (Long id : followerIds) {
                try {
                    java.util.Map<String, Object>[] allUsers = restTemplate.getForObject("http://localhost:8081/api/users/all", java.util.Map[].class);
                    if (allUsers != null) {
                        for (java.util.Map<String, Object> user : allUsers) {
                            if (user != null && user.containsKey("id") && ((Number) user.get("id")).longValue() == id) {
                                users.add(user);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {}
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }

    @GetMapping("/{userIdOrUsername}/following")
    public ResponseEntity<?> getFollowing(
            @PathVariable String userIdOrUsername,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId;
        try {
            userId = Long.parseLong(userIdOrUsername);
        } catch (NumberFormatException e) {
            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                java.util.Map<String, Object> user = restTemplate.getForObject("http://localhost:8081/api/users/" + userIdOrUsername, java.util.Map.class);
                if (user == null || !user.containsKey("id")) {
                    return ResponseEntity.ok(new java.util.ArrayList<>());
                }
                userId = ((Number) user.get("id")).longValue();
            } catch (Exception ex) {
                return ResponseEntity.ok(new java.util.ArrayList<>());
            }
        }
        try {
            List<Long> followingIds = followService.getFollowing(userId, page, size);
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.List<java.util.Map<String, Object>> users = new java.util.ArrayList<>();
            
            for (Long id : followingIds) {
                try {
                    java.util.Map<String, Object>[] allUsers = restTemplate.getForObject(
                        "http://localhost:8081/api/users/all", 
                        java.util.Map[].class
                    );
                    if (allUsers != null) {
                        for (java.util.Map<String, Object> user : allUsers) {
                            if (user != null && user.containsKey("id") && ((Number) user.get("id")).longValue() == id) {
                                users.add(user);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {}
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Long userId) {
        UserStatsResponse stats = followService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{userId}/graph")
    public ResponseEntity<FollowGraphResponse> getFollowGraph(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "50") int limit) {
        FollowGraphResponse graph = followService.getFollowGraph(userId, limit);
        return ResponseEntity.ok(graph);
    }

    @GetMapping("/{userId}/mutual")
    public ResponseEntity<List<Long>> getMutualConnections(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        List<Long> mutual = followService.getMutualConnections(userId, limit);
        return ResponseEntity.ok(mutual);
    }

    @DeleteMapping("/remove/{username}")
    public ResponseEntity<?> removeFollower(@PathVariable String username, @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        if (currentUserId == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not authenticated"));
        }
        
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> user = restTemplate.getForObject("http://localhost:8081/api/users/" + username, java.util.Map.class);
            
            if (user == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
            }
            
            Long followerId = ((Number) user.get("id")).longValue();
            
            FollowRequest followRequest = new FollowRequest();
            followRequest.setFollowerId(followerId);
            followRequest.setFollowingId(currentUserId);
            
            boolean removed = followService.unfollowUser(followRequest);
            if (removed) {
                return ResponseEntity.ok(java.util.Map.of("message", "Follower removed successfully"));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "Follower not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Internal server error"));
        }
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> followUserByUsername(@PathVariable String username, @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        if (currentUserId == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not authenticated"));
        }
        
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> user = restTemplate.getForObject("http://localhost:8081/api/users/" + username, java.util.Map.class);
            
            if (user == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
            }
            
            Long followingId = ((Number) user.get("id")).longValue();
            FollowRequest followRequest = new FollowRequest();
            followRequest.setFollowerId(currentUserId);
            followRequest.setFollowingId(followingId);
            followService.followUser(followRequest);
            
            return ResponseEntity.ok(java.util.Map.of("message", "User followed successfully"));
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Internal server error"));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> unfollowUserByUsername(@PathVariable String username, @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        if (currentUserId == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not authenticated"));
        }
        
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> user = restTemplate.getForObject("http://localhost:8081/api/users/" + username, java.util.Map.class);
            
            if (user == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
            }
            
            Long followingId = ((Number) user.get("id")).longValue();
            FollowRequest followRequest = new FollowRequest();
            followRequest.setFollowerId(currentUserId);
            followRequest.setFollowingId(followingId);
            followService.unfollowUser(followRequest);
            
            return ResponseEntity.ok(java.util.Map.of("message", "User unfollowed successfully"));
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Follow Service is running");
    }
}