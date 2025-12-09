package com.revhub.postservice.controller;

import com.revhub.postservice.dto.PostRequest;
import com.revhub.postservice.entity.Post;
import com.revhub.postservice.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            List<com.revhub.postservice.dto.PostResponse> posts = postService.getAllPosts(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", posts);
            response.put("totalElements", posts.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.revhub.postservice.dto.PostResponse> getPostById(@PathVariable Long id) {
        try {
            com.revhub.postservice.dto.PostResponse post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<com.revhub.postservice.dto.PostResponse> createPost(@Valid @RequestBody PostRequest postRequest) {
        try {
            com.revhub.postservice.dto.PostResponse post = postService.createPost(postRequest);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> createPostWithFile(
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "userId", required = false) Long userId,
            Authentication authentication) {
        try {
            System.out.println("Upload request - content: " + content);
            System.out.println("User ID: " + userId);
            System.out.println("File present: " + (file != null));
            if (file != null) {
                System.out.println("File name: " + file.getOriginalFilename());
                System.out.println("File size: " + file.getSize());
                System.out.println("Content type: " + file.getContentType());
            }
            
            PostRequest postRequest = new PostRequest();
            postRequest.setContent(content);
            postRequest.setUserId(userId != null ? userId : 1L);
            
            if (file != null && !file.isEmpty()) {
                String base64Image = java.util.Base64.getEncoder().encodeToString(file.getBytes());
                String mediaType = file.getContentType().startsWith("image/") ? "image" : "video";
                postRequest.setImageUrl("data:" + file.getContentType() + ";base64," + base64Image);
                postRequest.setMediaType(mediaType);
            }
            
            com.revhub.postservice.dto.PostResponse post = postService.createPost(postRequest);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.revhub.postservice.dto.PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest postRequest) {
        try {
            com.revhub.postservice.dto.PostResponse post = postService.updatePost(id, postRequest);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Post deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<com.revhub.postservice.dto.PostResponse>> getUserPosts(@PathVariable String username) {
        try {
            List<com.revhub.postservice.dto.PostResponse> posts = postService.getPostsByUsername(username);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        try {
            Long actualUserId = userId != null ? userId : 1L;
            Post post = postService.toggleLike(id, actualUserId);
            boolean isLiked = postService.isPostLikedByUser(id, actualUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("likesCount", post.getLikesCount());
            response.put("isLiked", isLiked);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        try {
            Long actualUserId = userId != null ? userId : 1L;
            Post post = postService.toggleLike(id, actualUserId);
            boolean isLiked = postService.isPostLikedByUser(id, actualUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("likesCount", post.getLikesCount());
            response.put("isLiked", isLiked);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Object>> sharePost(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        try {
            Long actualUserId = userId != null ? userId : 1L;
            Post post = postService.sharePost(id, actualUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("sharesCount", post.getSharesCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody Map<String, String> request, @RequestParam(required = false) Long userId) {
        try {
            String content = request.get("content");
            Long actualUserId = userId != null ? userId : 1L;
            Map<String, Object> comment = postService.addComment(id, content, actualUserId);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable Long id) {
        try {
            List<Map<String, Object>> comments = postService.getComments(id);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        try {
            postService.deleteComment(postId, commentId);
            return ResponseEntity.ok("Comment deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<?> addReply(@PathVariable Long commentId, @RequestBody Map<String, String> request, @RequestParam(required = false) Long userId) {
        try {
            String content = request.get("content");
            Long actualUserId = userId != null ? userId : 1L;
            Map<String, Object> reply = postService.addReply(commentId, content, actualUserId);
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<?> toggleCommentLike(@PathVariable Long commentId, @RequestParam(required = false) Long userId) {
        try {
            Long actualUserId = userId != null ? userId : 1L;
            com.revhub.postservice.entity.Comment comment = postService.toggleCommentLike(commentId, actualUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("likesCount", comment.getLikesCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<com.revhub.postservice.dto.PostResponse>> searchPosts(@RequestParam String query) {
        try {
            List<com.revhub.postservice.dto.PostResponse> posts = postService.searchPosts(query);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Post Service is running");
    }
}