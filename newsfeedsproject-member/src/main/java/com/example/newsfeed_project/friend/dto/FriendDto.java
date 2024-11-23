package com.example.newsfeed_project.friend.dto;

import com.example.newsfeed_project.friend.entity.Friend;
import com.example.newsfeed_project.member.dto.MemberDto;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto {
    private Long id;
    private boolean friendApproval; // 승인 여부
    private MemberDto member;


    // Entity에서 DTO로 변환하는 생성자
    public static com.example.newsfeed_project.friend.dto.FriendDto toDto(Friend friend) {
        return com.example.newsfeed_project.friend.dto.FriendDto.builder()
                .id(friend.getId())
                .member(MemberDto.toDto(friend.getMember()))
                .friendApproval(friend.isFriendApproval())
                .build();
    }

}