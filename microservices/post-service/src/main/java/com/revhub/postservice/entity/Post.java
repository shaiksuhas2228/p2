package com.revhub.postservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;
    
    private String mediaType;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "author_username", nullable = false)
    private String authorUsername;

    @Column(nullable = false)
    private Integer likesCount = 0;

    @Column(nullable = false)
    private Integer commentsCount = 0;

    @Column(nullable = false)
    private Integer sharesCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<com.revhub.postservice.entity.Media> mediaList;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<com.revhub.postservice.entity.Hashtag> hashtags;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<com.revhub.postservice.entity.Comment> comments;
}