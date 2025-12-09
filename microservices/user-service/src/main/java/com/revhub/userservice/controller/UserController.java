package com.revhub.userservice.controller;

import com.revhub.userservice.entity.User;
import com.revhub.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/id/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(user -> {
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("profilePicture", user.getProfilePicture());
                response.put("bio", user.getBio());
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
            .map(user -> {
                // Get follower/following counts from follow-service
                try {
                    org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                    java.util.Map<String, Object> stats = restTemplate.getForObject(
                        "http://localhost:8084/api/follow/" + user.getId() + "/stats",
                        java.util.Map.class
                    );
                    
                    java.util.Map<String, Object> response = new java.util.HashMap<>();
                    response.put("id", user.getId());
                    response.put("username", user.getUsername());
                    response.put("email", user.getEmail());
                    response.put("profilePicture", user.getProfilePicture());
                    response.put("bio", user.getBio());
                    response.put("isPrivate", user.getIsPrivate());
                    response.put("createdDate", user.getCreatedDate());
                    response.put("followersCount", stats.get("followersCount"));
                    response.put("followingCount", stats.get("followingCount"));
                    
                    return ResponseEntity.ok(response);
                } catch (Exception e) {
                    return ResponseEntity.ok(user);
                }
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        return ResponseEntity.ok(users);
    }
    

    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody java.util.Map<String, Object> updates) {
        return updateUser(updates);
    }
    
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody java.util.Map<String, Object> updates) {
        try {
            String username = (String) updates.get("username");
            if (username == null) {
                User currentUser = (User) updates.get("user");
                if (currentUser != null) username = currentUser.getUsername();
            }
            
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                Long userId = updates.get("id") != null ? ((Number) updates.get("id")).longValue() : 1L;
                user = userRepository.findById(userId).orElse(null);
            }
            
            if (user == null) return ResponseEntity.notFound().build();
            
            if (updates.containsKey("bio")) user.setBio((String) updates.get("bio"));
            if (updates.containsKey("profilePicture")) user.setProfilePicture((String) updates.get("profilePicture"));
            if (updates.containsKey("isPrivate")) user.setIsPrivate(Boolean.parseBoolean(updates.get("isPrivate").toString()));
            
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
