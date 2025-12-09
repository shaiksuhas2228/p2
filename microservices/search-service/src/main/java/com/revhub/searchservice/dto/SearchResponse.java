package com.revhub.searchservice.dto;

import java.util.List;

public class SearchResponse {
    private List<PostSearchResult> posts;
    private List<UserSearchResult> users;
    private long totalPosts;
    private long totalUsers;
    private String query;
    private long searchTime;

    // Getters and Setters
    public List<PostSearchResult> getPosts() { return posts; }
    public void setPosts(List<PostSearchResult> posts) { this.posts = posts; }

    public List<UserSearchResult> getUsers() { return users; }
    public void setUsers(List<UserSearchResult> users) { this.users = users; }

    public long getTotalPosts() { return totalPosts; }
    public void setTotalPosts(long totalPosts) { this.totalPosts = totalPosts; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public long getSearchTime() { return searchTime; }
    public void setSearchTime(long searchTime) { this.searchTime = searchTime; }

    public static class PostSearchResult {
        private Long postId;
        private String content;
        private String username;
        private List<String> hashtags;
        private String createdAt;
        private Integer likesCount;
        private Integer commentsCount;

        // Getters and Setters
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public List<String> getHashtags() { return hashtags; }
        public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public Integer getLikesCount() { return likesCount; }
        public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

        public Integer getCommentsCount() { return commentsCount; }
        public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }
    }

    public static class UserSearchResult {
        private Long userId;
        private String username;
        private String bio;
        private String profilePicture;
        private Integer followersCount;
        private Integer postsCount;
        private Boolean verified;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }

        public String getProfilePicture() { return profilePicture; }
        public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

        public Integer getFollowersCount() { return followersCount; }
        public void setFollowersCount(Integer followersCount) { this.followersCount = followersCount; }

        public Integer getPostsCount() { return postsCount; }
        public void setPostsCount(Integer postsCount) { this.postsCount = postsCount; }

        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }
    }
}