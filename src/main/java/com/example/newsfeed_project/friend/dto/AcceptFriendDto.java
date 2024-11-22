package com.example.newsfeed_project.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class AcceptFriendDto {
    private Long requestId;
    private boolean isAccepted;
}
