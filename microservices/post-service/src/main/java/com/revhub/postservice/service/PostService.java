package com.revhub.postservice.service;

import com.revhub.postservice.dto.*;
import com.revhub.postservice.entity.*;
import com.revhub.postservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private com.revhub.postservice.repository.CommentRepository commentRepository;

    @Autowired
    private com.revhub.postservice.repository.PostLikeRepository postLikeRepository;

    public PostResponse createPost(PostRequest request) {
        Post post = new Post();
        post.setContent(request.getContent());
        post.setUserId(request.getUserId());
        post.setAuthorId(request.getUserId());
        
        // Fetch user details from user service by ID
        try {
            RestTemplate restTemplate = new RestTemplate();
            // First get all users and find by ID
            List<Map<String, Object>> users = restTemplate.getForObject(
                "http://localhost:8081/api/users/all",
                List.class
            );
            Map<String, Object> user = users.stream()
                .filter(u -> ((Number)u.get("id")).longValue() == request.getUserId())
                .findFirst()
                .orElse(null);
            
            if (user != null) {
                post.setAuthorUsername((String) user.get("username"));
            } else {
                post.setAuthorUsername("user" + request.getUserId());
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch user: " + e.getMessage());
            post.setAuthorUsername("user" + request.getUserId());
        }
        
        post.setImageUrl(request.getImageUrl());
        post.setMediaType(request.getMediaType());
        
        Post savedPost = postRepository.save(post);
        
        if (request.getHashtags() != null) {
            for (String tagName : request.getHashtags()) {
                Hashtag hashtag = new Hashtag();
                hashtag.setTagName(tagName.startsWith("#") ? tagName : "#" + tagName);
                hashtag.setPost(savedPost);
                hashtagRepository.save(hashtag);
            }
        }
        
        return convertToResponse(savedPost);
    }

    public PostResponse addMediaToPost(Long postId, MultipartFile[] files) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        mediaService.uploadMedia(postId, files);
        return convertToResponse(post);
    }

    public List<PostResponse> getAllPosts(Long userId) {
        return postRepository.findAllOrderByCreatedAtDesc()
            .stream()
            .map(post -> convertToResponse(post, userId))
            .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByUser(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByHashtag(String hashtag) {
        String tagName = hashtag.startsWith("#") ? hashtag : "#" + hashtag;
        return postRepository.findByHashtagOrderByCreatedAtDesc(tagName)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    private PostResponse convertToResponse(Post post) {
        return convertToResponse(post, null);
    }
    
    private PostResponse convertToResponse(Post post, Long userId) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setUserId(post.getUserId());
        response.setAuthorUsername(post.getAuthorUsername());
        response.setImageUrl(post.getImageUrl());
        response.setMediaType(post.getMediaType());
        response.setLikesCount(post.getLikesCount());
        response.setCommentsCount(post.getCommentsCount());
        response.setSharesCount(post.getSharesCount());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        
        // Fetch author details from user-service
        try {
            RestTemplate restTemplate = new RestTemplate();
            String username = post.getAuthorUsername();
            if (username != null && !username.isEmpty()) {
                Map<String, Object> user = restTemplate.getForObject(
                    "http://localhost:8081/api/users/" + username,
                    Map.class
                );
                if (user != null) {
                    Map<String, Object> author = new HashMap<>();
                    author.put("id", user.get("id"));
                    author.put("username", user.get("username"));
                    author.put("profilePicture", user.get("profilePicture"));
                    response.setAuthor(author);
                } else {
                    Map<String, Object> author = new HashMap<>();
                    author.put("id", post.getUserId());
                    author.put("username", username);
                    author.put("profilePicture", null);
                    response.setAuthor(author);
                }
            } else {
                Map<String, Object> author = new HashMap<>();
                author.put("id", post.getUserId());
                author.put("username", "user" + post.getUserId());
                author.put("profilePicture", null);
                response.setAuthor(author);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch author details: " + e.getMessage());
            Map<String, Object> author = new HashMap<>();
            author.put("id", post.getUserId());
            author.put("username", post.getAuthorUsername() != null ? post.getAuthorUsername() : "user" + post.getUserId());
            author.put("profilePicture", null);
            response.setAuthor(author);
        }
        
        if (userId != null) {
            response.setIsLikedByCurrentUser(isPostLikedByUser(post.getId(), userId));
        }
        
        if (post.getMediaList() != null) {
            response.setMedia(post.getMediaList().stream()
                .map(this::convertMediaToResponse)
                .collect(Collectors.toList()));
        }
        
        if (post.getHashtags() != null) {
            response.setHashtags(post.getHashtags().stream()
                .map(Hashtag::getTagName)
                .collect(Collectors.toList()));
        }
        
        return response;
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        return convertToResponse(post);
    }

    public PostResponse updatePost(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getImageUrl() != null) {
            post.setImageUrl(request.getImageUrl());
        }
        if (request.getMediaType() != null) {
            post.setMediaType(request.getMediaType());
        }
        
        Post updatedPost = postRepository.save(post);
        return convertToResponse(updatedPost);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public List<PostResponse> getPostsByUsername(String username) {
        // Get user ID from user-service
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> user = restTemplate.getForObject(
                "http://localhost:8081/api/users/" + username,
                java.util.Map.class
            );
            Long userId = ((Number) user.get("id")).longValue();
            
            return postRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    public Post toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Check if user already liked
        Optional<com.revhub.postservice.entity.PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        
        if (existingLike.isPresent()) {
            // Unlike
            postLikeRepository.delete(existingLike.get());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        } else {
            // Like
            com.revhub.postservice.entity.PostLike like = new com.revhub.postservice.entity.PostLike();
            like.setPost(post);
            like.setUserId(userId);
            postLikeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
            
            // Send notification only if not liking own post
            if (!userId.equals(post.getUserId())) {
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("userId", post.getUserId());
                    notification.put("title", "New Like");
                    notification.put("message", "liked your post");
                    notification.put("type", "LIKE");
                    notification.put("priority", "LOW");
                    notification.put("relatedEntityId", postId);
                    notification.put("relatedEntityType", "POST");
                    
                    System.out.println("Sending like notification for post " + postId + " owned by user " + post.getUserId());
                    restTemplate.postForObject("http://localhost:8085/api/notifications", notification, Map.class);
                    System.out.println("Like notification sent successfully");
                } catch (Exception e) {
                    System.out.println("Failed to send like notification: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        return postRepository.save(post);
    }

    public Post sharePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setSharesCount(post.getSharesCount() + 1);
        
        if (!userId.equals(post.getUserId())) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> notification = new HashMap<>();
                notification.put("userId", post.getUserId());
                notification.put("title", "Post Shared");
                notification.put("message", "shared your post");
                notification.put("type", "SHARE");
                notification.put("priority", "MEDIUM");
                notification.put("relatedEntityId", postId);
                notification.put("relatedEntityType", "POST");
                restTemplate.postForObject("http://localhost:8085/api/notifications", notification, Map.class);
            } catch (Exception e) {
                System.out.println("Failed to send share notification: " + e.getMessage());
            }
        }
        
        return postRepository.save(post);
    }

    public Map<String, Object> addComment(Long postId, String content, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        com.revhub.postservice.entity.Comment comment = new com.revhub.postservice.entity.Comment();
        comment.setContent(content);
        comment.setUserId(userId);
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> user = restTemplate.getForObject(
                "http://localhost:8081/api/users/id/" + userId,
                Map.class
            );
            String username = user != null && user.get("username") != null ? (String) user.get("username") : "user" + userId;
            comment.setAuthorUsername(username);
        } catch (Exception e) {
            comment.setAuthorUsername("user" + userId);
        }
        
        comment.setPost(post);
        
        com.revhub.postservice.entity.Comment saved = commentRepository.save(comment);
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
        
        if (!userId.equals(post.getUserId())) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> notification = new HashMap<>();
                notification.put("userId", post.getUserId());
                notification.put("title", "New Comment");
                notification.put("message", "commented on your post");
                notification.put("type", "COMMENT");
                notification.put("priority", "MEDIUM");
                notification.put("relatedEntityId", postId);
                notification.put("relatedEntityType", "POST");
                restTemplate.postForObject("http://localhost:8085/api/notifications", notification, Map.class);
            } catch (Exception e) {
                System.out.println("Failed to send comment notification: " + e.getMessage());
            }
        }
        
        return convertCommentToMap(saved);
    }

    public List<Map<String, Object>> getComments(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        List<com.revhub.postservice.entity.Comment> comments = commentRepository.findByPostIdAndParentCommentIsNull(postId);
        System.out.println("Found " + comments.size() + " comments for post " + postId);
        return comments.stream().map(this::convertCommentToMap).collect(Collectors.toList());
    }

    public void deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        commentRepository.deleteById(commentId);
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        postRepository.save(post);
    }

    public Map<String, Object> addReply(Long commentId, String content, Long userId) {
        com.revhub.postservice.entity.Comment parentComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        com.revhub.postservice.entity.Comment reply = new com.revhub.postservice.entity.Comment();
        reply.setContent(content);
        reply.setUserId(userId);
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> user = restTemplate.getForObject(
                "http://localhost:8081/api/users/id/" + userId,
                Map.class
            );
            String username = user != null && user.get("username") != null ? (String) user.get("username") : "user" + userId;
            reply.setAuthorUsername(username);
        } catch (Exception e) {
            reply.setAuthorUsername("user" + userId);
        }
        
        reply.setParentComment(parentComment);
        reply.setPost(parentComment.getPost());
        
        com.revhub.postservice.entity.Comment saved = commentRepository.save(reply);
        
        if (!userId.equals(parentComment.getUserId())) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> notification = new HashMap<>();
                notification.put("userId", parentComment.getUserId());
                notification.put("title", "New Reply");
                notification.put("message", "replied to your comment");
                notification.put("type", "COMMENT");
                notification.put("priority", "MEDIUM");
                notification.put("relatedEntityId", commentId);
                notification.put("relatedEntityType", "COMMENT");
                restTemplate.postForObject("http://localhost:8085/api/notifications", notification, Map.class);
            } catch (Exception e) {
                System.out.println("Failed to send reply notification: " + e.getMessage());
            }
        }
        
        return convertCommentToMap(saved);
    }

    public com.revhub.postservice.entity.Comment toggleCommentLike(Long commentId, Long userId) {
        com.revhub.postservice.entity.Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setLikesCount(comment.getLikesCount() + 1);
        com.revhub.postservice.entity.Comment saved = commentRepository.save(comment);
        
        if (!userId.equals(comment.getUserId())) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> notification = new HashMap<>();
                notification.put("userId", comment.getUserId());
                notification.put("title", "Comment Liked");
                notification.put("message", "liked your comment");
                notification.put("type", "LIKE");
                notification.put("priority", "LOW");
                notification.put("relatedEntityId", commentId);
                notification.put("relatedEntityType", "COMMENT");
                restTemplate.postForObject("http://localhost:8085/api/notifications", notification, Map.class);
            } catch (Exception e) {
                System.out.println("Failed to send comment like notification: " + e.getMessage());
            }
        }
        
        return saved;
    }

    private Map<String, Object> convertCommentToMap(com.revhub.postservice.entity.Comment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("content", comment.getContent());
        map.put("likesCount", comment.getLikesCount());
        map.put("createdDate", comment.getCreatedAt().toString());
        
        Map<String, Object> author = new HashMap<>();
        author.put("id", comment.getUserId());
        author.put("username", comment.getAuthorUsername());
        author.put("profilePicture", comment.getAuthorProfilePicture());
        map.put("author", author);
        
        // Load replies from database only if this is not already a reply
        List<Map<String, Object>> replyMaps = new ArrayList<>();
        if (comment.getParentComment() == null) {
            List<com.revhub.postservice.entity.Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(comment.getId());
            System.out.println("Found " + replies.size() + " replies for comment " + comment.getId());
            replyMaps = replies.stream()
                .map(reply -> {
                    Map<String, Object> replyMap = new HashMap<>();
                    replyMap.put("id", reply.getId());
                    replyMap.put("content", reply.getContent());
                    replyMap.put("likesCount", reply.getLikesCount());
                    replyMap.put("createdDate", reply.getCreatedAt().toString());
                    
                    Map<String, Object> replyAuthor = new HashMap<>();
                    replyAuthor.put("id", reply.getUserId());
                    replyAuthor.put("username", reply.getAuthorUsername());
                    replyAuthor.put("profilePicture", reply.getAuthorProfilePicture());
                    replyMap.put("author", replyAuthor);
                    replyMap.put("replies", new ArrayList<>());
                    
                    return replyMap;
                })
                .collect(Collectors.toList());
        }
        map.put("replies", replyMaps);
        
        return map;
    }

    public List<PostResponse> searchPosts(String query) {
        List<Post> posts = postRepository.findByContentContainingIgnoreCase(query);
        return posts.stream().map(this::convertToResponse).collect(java.util.stream.Collectors.toList());
    }

    public boolean isPostLikedByUser(Long postId, Long userId) {
        return postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
    
    private MediaResponse convertMediaToResponse(Media media) {
        MediaResponse response = new MediaResponse();
        response.setId(media.getId());
        response.setFileName(media.getFileName());
        response.setFilePath(media.getFilePath());
        response.setFileType(media.getFileType());
        response.setFileSize(media.getFileSize());
        return response;
    }
}