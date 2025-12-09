package com.revhub.feedservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FeedResponse {
    private Long postId;
    private Long userId;
    private String content;
    private List<String> hashtags;
    private List<MediaResponse> media;
    private LocalDateTime createdAt;
    private Double score;
    private String authorUsername;
    private String imageUrl;
    private String mediaType;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private Map<String, Object> author;

    // Getters and Setters
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }

    public List<MediaResponse> getMedia() { return media; }
    public void setMedia(List<MediaResponse> media) { this.media = media; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

    public Integer getCommentsCount() { return commentsCount; }
    public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }

    public Integer getSharesCount() { return sharesCount; }
    public void setSharesCount(Integer sharesCount) { this.sharesCount = sharesCount; }

    public Map<String, Object> getAuthor() { return author; }
    public void setAuthor(Map<String, Object> author) { this.author = author; }
}

class MediaResponse {
    private String fileName;
    private String filePath;
    private String fileType;

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}