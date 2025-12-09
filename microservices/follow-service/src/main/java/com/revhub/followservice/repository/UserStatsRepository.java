package com.revhub.followservice.repository;

import com.revhub.followservice.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    
    @Modifying
    @Transactional
    @Query("UPDATE UserStats u SET u.followingCount = u.followingCount + 1 WHERE u.userId = :userId")
    int incrementFollowingCount(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserStats u SET u.followingCount = u.followingCount - 1 WHERE u.userId = :userId AND u.followingCount > 0")
    int decrementFollowingCount(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserStats u SET u.followersCount = u.followersCount + 1 WHERE u.userId = :userId")
    int incrementFollowersCount(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserStats u SET u.followersCount = u.followersCount - 1 WHERE u.userId = :userId AND u.followersCount > 0")
    int decrementFollowersCount(@Param("userId") Long userId);
}