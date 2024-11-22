package com.example.newsfeed_project.friend.repository;

import com.example.newsfeed_project.friend.entity.Friend;
import com.example.newsfeed_project.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    //무조건 pk 값
    @Query("SELECT f FROM Friend f WHERE (f.requestFriend.id = :userId OR f.responseFriend.id = :userId) AND f.status = :status")
    Page<Friend> findApprovedFriendsByUserId(@Param("userId") Long userId, @Param("status") String status, Pageable pageable);
    Optional<Friend> findByRequestFriendIdAndResponseFriendId(Long requestId, Long responseId);

    Optional<Friend> findByRequestFriendAndResponseFriend(Member requestFriend, Member responseFriend);
}
