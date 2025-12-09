package com.revhub.followservice.service;

import com.revhub.followservice.dto.*;
import com.revhub.followservice.entity.Follow;
import com.revhub.followservice.entity.UserStats;
import com.revhub.followservice.repository.FollowRepository;
import com.revhub.followservice.repository.UserStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FollowService {

    private static final Logger logger = LoggerFactory.getLogger(FollowService.class);

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserStatsRepository userStatsRepository;

    public boolean followUser(FollowRequest request) {
        if (request == null || request.getFollowerId() == null || request.getFollowingId() == null) {
            throw new IllegalArgumentException("Invalid follow request");
        }
        
        if (request.getFollowerId().equals(request.getFollowingId())) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(
                request.getFollowerId(), request.getFollowingId())) {
            return false;
        }

        Follow follow = new Follow();
        follow.setFollowerId(request.getFollowerId());
        follow.setFollowingId(request.getFollowingId());
        followRepository.save(follow);

        // Update stats
        updateUserStats(request.getFollowerId());
        updateUserStats(request.getFollowingId());
        userStatsRepository.incrementFollowingCount(request.getFollowerId());
        userStatsRepository.incrementFollowersCount(request.getFollowingId());

        // Send notification
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> notification = new java.util.HashMap<>();
            notification.put("userId", request.getFollowingId());
            notification.put("title", "New Follower");
            notification.put("message", "started following you");
            notification.put("type", "FOLLOW");
            notification.put("priority", "MEDIUM");
            notification.put("relatedEntityId", request.getFollowerId());
            notification.put("relatedEntityType", "USER");
            notification.put("followRequestId", follow.getId());
            restTemplate.postForObject("http://localhost:8085/api/notifications", notification, java.util.Map.class);
        } catch (Exception e) {
            logger.warn("Failed to send notification for follow request", e);
        }

        return true;
    }

    public boolean unfollowUser(FollowRequest request) {
        if (request == null || request.getFollowerId() == null || request.getFollowingId() == null) {
            throw new IllegalArgumentException("Invalid unfollow request");
        }
        
        Optional<Follow> follow = followRepository.findByFollowerIdAndFollowingId(
                request.getFollowerId(), request.getFollowingId());

        if (follow.isEmpty()) {
            return false;
        }

        followRepository.delete(follow.get());
        followRepository.flush();

        userStatsRepository.decrementFollowingCount(request.getFollowerId());
        userStatsRepository.decrementFollowersCount(request.getFollowingId());
        
        return true;
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            return false;
        }
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public List<Long> getFollowers(Long userId, int page, int size) {
        if (userId == null) {
            return List.of();
        }
        return followRepository.findFollowerIds(userId, PageRequest.of(page, size));
    }

    public List<Long> getFollowing(Long userId, int page, int size) {
        if (userId == null) {
            return List.of();
        }
        return followRepository.findFollowingIds(userId, PageRequest.of(page, size));
    }

    public UserStatsResponse getUserStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        long followersCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        
        UserStatsResponse response = new UserStatsResponse();
        response.setUserId(userId);
        response.setFollowersCount(followersCount);
        response.setFollowingCount(followingCount);
        return response;
    }

    public FollowGraphResponse getFollowGraph(Long userId, int limit) {
        FollowGraphResponse response = new FollowGraphResponse();
        response.setUserId(userId);
        response.setFollowers(getFollowers(userId, 0, limit));
        response.setFollowing(getFollowing(userId, 0, limit));
        response.setMutualConnections(getMutualConnections(userId, limit));
        response.setStats(getUserStats(userId));
        return response;
    }

    public List<Long> getMutualConnections(Long userId, int limit) {
        List<Long> following = followRepository.findFollowingIds(userId, PageRequest.of(0, 1000));
        if (following.isEmpty()) {
            return List.of();
        }
        return followRepository.findMutualConnections(following).stream()
                .distinct()
                .limit(limit)
                .toList();
    }

    private void updateUserStats(Long userId) {
        if (!userStatsRepository.existsById(userId)) {
            UserStats stats = createDefaultStats(userId);
            userStatsRepository.save(stats);
        }
    }

    private UserStats createDefaultStats(Long userId) {
        UserStats stats = new UserStats();
        stats.setUserId(userId);
        stats.setFollowersCount(0L);
        stats.setFollowingCount(0L);
        return stats;
    }
}