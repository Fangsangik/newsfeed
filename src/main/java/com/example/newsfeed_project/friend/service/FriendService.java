package com.example.newsfeed_project.friend.service;

import com.example.newsfeed_project.friend.dto.FriendDto;
import org.springframework.data.domain.Page;

public interface FriendService {
    void sendFriendRequest(FriendDto friendDto, Long loggedInUserId);
    Page<FriendDto> getApprovedFriendList(int page, int size, Long loggedInUserId);
    void deleteFriendByResponseId(Long requestId, Long responseId);
    void acceptFriendRequest(Long requestId, boolean isApproved, Long loggedInUserId);
}
