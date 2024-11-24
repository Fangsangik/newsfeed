package com.example.newsfeed_project.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendResponseDto {
    // 요청자, 응답자 id와 approval 여부
    private Long requestFriendId;
    private Long responseFriendId;
    private boolean friendApproval;
}