package com.example.newsfeed_project.friend.service;

import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedResponseDto;
import org.springframework.data.domain.Page;

public interface FriendService {
    void sendFriendRequest(FriendDto friendDto, String loggedInUserEmail);
    void acceptFriendRequest(Long requestId, Long responseFriendId, boolean isApproved, String loggedInUserEmail);
    void deleteFriendByResponseId(Long requestId, Long responseId);
    Page<FriendDto> getApprovedFriendList(int page, int size, String email);
    Page<NewsfeedResponseDto> findFriendsNewsfeed(int page, int size, String email);
}
