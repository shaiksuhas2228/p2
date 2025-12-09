package com.revhub.followservice.dto;

public class UserStatsResponse {
    private Long userId;
    private Long followersCount;
    private Long followingCount;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFollowersCount() { return followersCount; }
    public void setFollowersCount(Long followersCount) { this.followersCount = followersCount; }

    public Long getFollowingCount() { return followingCount; }
    public void setFollowingCount(Long followingCount) { this.followingCount = followingCount; }
}