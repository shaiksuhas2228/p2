package com.revhub.feedservice.service;

import com.revhub.feedservice.dto.FeedResponse;
import com.revhub.feedservice.dto.PostEvent;
import com.revhub.feedservice.entity.FeedItem;
import com.revhub.feedservice.repository.FeedItemRepository;
import com.revhub.feedservice.repository.UserFollowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class FeedService {

    @Autowired
    private FeedItemRepository feedItemRepository;

    @Autowired
    private UserFollowingRepository userFollowingRepository;

    @Autowired
    private PostServiceClient postServiceClient;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Cacheable(value = "user-feed", key = "#userId + '-' + #page + '-' + #size")
    public List<FeedResponse> getPersonalizedFeed(Long userId, int page, int size) {
        // Try Redis cache first
        String cacheKey = "feed:" + userId + ":" + page + ":" + size;
        List<FeedResponse> cachedFeed = getCachedFeed(cacheKey);
        if (cachedFeed != null) {
            return cachedFeed;
        }
        
        // Generate fresh feed
        List<FeedItem> feedItems = feedItemRepository.findPersonalizedFeed(
            userId, PageRequest.of(page, size));
        
        List<FeedResponse> feed = feedItems.stream()
            .map(this::enrichFeedItem)
            .toList();
        
        // Cache the result
        cacheFeed(cacheKey, feed);
        return feed;
    }

    public void processNewPost(PostEvent postEvent) {
        List<Long> followers = userFollowingRepository.findFollowerIds(postEvent.getUserId());
        
        for (Long followerId : followers) {
            double score = calculateScore(postEvent, followerId);
            
            FeedItem feedItem = new FeedItem();
            feedItem.setUserId(followerId);
            feedItem.setPostId(postEvent.getPostId());
            feedItem.setPostUserId(postEvent.getUserId());
            feedItem.setScore(score);
            
            feedItemRepository.save(feedItem);
            
            // Invalidate cache
            evictUserFeedCache(followerId);
        }
    }

    @CacheEvict(value = "user-feed", allEntries = true)
    public void removePostFromFeeds(Long postId) {
        List<FeedItem> feedItems = feedItemRepository.findAll();
        feedItems.stream()
            .filter(item -> item.getPostId().equals(postId))
            .forEach(item -> {
                feedItemRepository.delete(item);
                evictUserFeedCache(item.getUserId());
            });
    }

    public void followUser(Long followerId, Long followingId) {
        // Add recent posts from followed user to follower's feed
        List<FeedResponse> recentPosts = postServiceClient.getRecentPostsByUser(followingId, 10);
        
        for (FeedResponse post : recentPosts) {
            if (!feedItemRepository.existsByUserIdAndPostId(followerId, post.getPostId())) {
                FeedItem feedItem = new FeedItem();
                feedItem.setUserId(followerId);
                feedItem.setPostId(post.getPostId());
                feedItem.setPostUserId(followingId);
                feedItem.setScore(calculateFollowScore(post));
                
                feedItemRepository.save(feedItem);
            }
        }
        
        evictUserFeedCache(followerId);
    }

    private double calculateScore(PostEvent postEvent, Long userId) {
        double score = 1.0;
        
        // Time decay
        long hoursOld = java.time.Duration.between(postEvent.getCreatedAt(), 
            java.time.LocalDateTime.now()).toHours();
        score *= Math.exp(-hoursOld * 0.1);
        
        // Hashtag relevance
        if (postEvent.getHashtags() != null && !postEvent.getHashtags().isEmpty()) {
            score *= 1.2;
        }
        
        return score;
    }

    private double calculateFollowScore(FeedResponse post) {
        return 0.8; // Base score for followed user posts
    }

    private FeedResponse enrichFeedItem(FeedItem feedItem) {
        FeedResponse response = postServiceClient.getPostDetails(feedItem.getPostId());
        if (response != null) {
            response.setScore(feedItem.getScore());
        }
        return response;
    }

    private void evictUserFeedCache(Long userId) {
        if (redisTemplate != null) {
            String pattern = "user-feed::" + userId + "-*";
            redisTemplate.delete(redisTemplate.keys(pattern));
            
            // Also clear direct cache keys
            String feedPattern = "feed:" + userId + ":*";
            redisTemplate.delete(redisTemplate.keys(feedPattern));
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<FeedResponse> getCachedFeed(String key) {
        if (redisTemplate == null) return null;
        try {
            return (List<FeedResponse>) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }
    
    private void cacheFeed(String key, List<FeedResponse> feed) {
        if (redisTemplate == null) return;
        try {
            redisTemplate.opsForValue().set(key, feed, java.time.Duration.ofMinutes(30));
        } catch (Exception e) {
            System.err.println("Failed to cache feed: " + e.getMessage());
        }
    }
}