package com.revhub.followservice.repository;

import com.revhub.followservice.entity.Follow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    @Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :userId ORDER BY f.createdAt DESC")
    List<Long> findFollowingIds(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT f.followerId FROM Follow f WHERE f.followingId = :userId ORDER BY f.createdAt DESC")
    List<Long> findFollowerIds(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followerId = :userId")
    Long countFollowing(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followingId = :userId")
    Long countFollowers(@Param("userId") Long userId);
    
    long countByFollowerId(Long followerId);
    
    long countByFollowingId(Long followingId);
    
    @Query("SELECT f.followingId FROM Follow f WHERE f.followerId IN :userIds")
    List<Long> findMutualConnections(@Param("userIds") List<Long> userIds);
    
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}