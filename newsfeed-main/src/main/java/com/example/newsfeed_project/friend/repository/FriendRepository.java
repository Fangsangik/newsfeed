package com.example.newsfeed_project.friend.repository;

import com.example.newsfeed_project.friend.entity.Friend;
import com.example.newsfeed_project.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT f FROM Friend f WHERE (f.requestFriend.email = :email OR f.responseFriend.email = :email) AND f.status = :status")
    Page<Friend> findApprovedFriendsByEmail(@Param("email") String email, @Param("status") String status, Pageable pageable);

    Optional<Friend> findByRequestFriendIdAndResponseFriendId(Long requestId, Long responseId);

    @Query("SELECT CASE WHEN f.requestFriend.email = :email THEN f.responseFriend.id ELSE f.requestFriend.id END " +
            "FROM Friend f WHERE f.status = :status AND (f.requestFriend.email = :email OR f.responseFriend.email = :email)")
    List<Long> findApprovedFriendIdsByEmail(String email, @Param("status")String status);

    @Query("SELECT f FROM Friend f WHERE f.id = :requestId AND f.status = :status")
    Optional<Friend> findByIdAndStatus(@Param("requestId") Long requestId, @Param("status") String status);

    Optional<Friend> findByRequestFriendAndResponseFriend(Member requestFriend, Member responseFriend);
}
