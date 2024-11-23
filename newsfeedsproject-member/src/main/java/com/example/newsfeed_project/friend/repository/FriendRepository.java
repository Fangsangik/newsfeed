package com.example.newsfeed_project.friend.repository;

import com.example.newsfeed_project.friend.entity.Friend;
import com.example.newsfeed_project.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByEmail(String email);
    Friend findByMember(Member member);
    Page<Friend> findByApprovalAndResponseMember(boolean friendApproval, Friend friend, Pageable pageable);
}
