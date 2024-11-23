package com.example.newsfeed_project.friend.dto;

import com.example.newsfeed_project.friend.entity.Friend;
import jakarta.validation.constraints.NotNull;

public class FriendRequestDto {

    @NotNull(message = "email is required")
    private String friendEmail;

    public FriendRequestDto(Friend friend) {
        this.friendEmail = friend.getMember().getEmail();
    }
}
