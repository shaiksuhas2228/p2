package com.revhub.followservice.dto;

import java.util.List;

public class FollowGraphResponse {
    private Long userId;
    private List<Long> followers;
    private List<Long> following;
    private List<Long> mutualConnections;
    private UserStatsResponse stats;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<Long> getFollowers() { return followers; }
    public void setFollowers(List<Long> followers) { this.followers = followers; }

    public List<Long> getFollowing() { return following; }
    public void setFollowing(List<Long> following) { this.following = following; }

    public List<Long> getMutualConnections() { return mutualConnections; }
    public void setMutualConnections(List<Long> mutualConnections) { this.mutualConnections = mutualConnections; }

    public UserStatsResponse getStats() { return stats; }
    public void setStats(UserStatsResponse stats) { this.stats = stats; }
}