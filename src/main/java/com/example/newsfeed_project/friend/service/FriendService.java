package com.example.newsfeed_project.friend.service;

import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FriendService {
    void sendFriendRequest(FriendDto friendDto, Long loggedInUserId);
    Page<FriendDto> getApprovedFriendList(int page, int size, Long loggedInUserId);
    Page<NewsfeedResponseDto> getFriendsNewsfeed(Long loggedInUserId, boolean isLike, Pageable pageable);
    void deleteFriendByResponseId(Long requestId, Long responseId);
    void acceptFriendRequest(Long requestId, boolean isApproved, Long loggedInUserId);
}