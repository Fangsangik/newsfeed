package com.example.newsfeed_project.friend.dto;

import com.example.newsfeed_project.friend.entity.Friend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class FriendDto {
    private Long id;
    private Long requestFriendId;
    private Long responseFriendId;
    private String image;
    private String email;
    private String name;
    private Boolean isApproval;

    public FriendDto(Friend friend) {
        if (friend == null || friend.getRequestFriend() == null || friend.getResponseFriend() == null) {
            throw new IllegalArgumentException("Friend 또는 Member 정보가 올바르지 않습니다.");
        }
        this.requestFriendId = friend.getRequestFriend().getId();
        this.responseFriendId = friend.getResponseFriend().getId();
        this.image = friend.getRequestFriend().getImage(); // 요청자의 이미지
        this.email = friend.getRequestFriend().getEmail(); // 요청자의 이메일
        this.name = friend.getRequestFriend().getName();   // 요청자의 이름
        this.isApproval = friend.isFriendApproval();
    }
}
