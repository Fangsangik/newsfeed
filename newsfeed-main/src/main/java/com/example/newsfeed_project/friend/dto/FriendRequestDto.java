package com.example.newsfeed_project.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendRequestDto {

    private Long requestFriendId;
    private Long responseFriendId;

}
