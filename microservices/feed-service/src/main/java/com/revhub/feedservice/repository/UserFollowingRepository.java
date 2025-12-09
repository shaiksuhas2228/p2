package com.revhub.feedservice.repository;

import com.revhub.feedservice.entity.UserFollowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserFollowingRepository extends JpaRepository<UserFollowing, Long> {
    
    @Query("SELECT uf.followingId FROM UserFollowing uf WHERE uf.followerId = :userId")
    List<Long> findFollowingIds(@Param("userId") Long userId);
    
    @Query("SELECT uf.followerId FROM UserFollowing uf WHERE uf.followingId = :userId")
    List<Long> findFollowerIds(@Param("userId") Long userId);
}