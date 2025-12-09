package com.revhub.followservice.dto;

import jakarta.validation.constraints.NotNull;

public class FollowRequest {
    @NotNull
    private Long followerId;

    @NotNull
    private Long followingId;

    // Getters and Setters
    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    public Long getFollowingId() { return followingId; }
    public void setFollowingId(Long followingId) { this.followingId = followingId; }
}