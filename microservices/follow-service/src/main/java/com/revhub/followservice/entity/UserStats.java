package com.revhub.followservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @Column(name = "followers_count", nullable = false)
    @NotNull(message = "Followers count cannot be null")
    @PositiveOrZero(message = "Followers count must be zero or positive")
    private Long followersCount = 0L;

    @Column(name = "following_count", nullable = false)
    @NotNull(message = "Following count cannot be null")
    @PositiveOrZero(message = "Following count must be zero or positive")
    private Long followingCount = 0L;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFollowersCount() { return followersCount; }
    public void setFollowersCount(Long followersCount) { this.followersCount = followersCount; }

    public Long getFollowingCount() { return followingCount; }
    public void setFollowingCount(Long followingCount) { this.followingCount = followingCount; }
}