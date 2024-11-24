package com.example.newsfeed_project.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Setter
@Getter
@AllArgsConstructor
public class AcceptFriendDto {
    private Long requestId;
    private boolean isAccepted;
}