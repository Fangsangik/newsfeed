package com.example.newsfeed_project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateResponseDto {
    private String image;
    private String name;
    private String phoneNumber;
    private String address;
    private LocalDateTime updatedAt;


    public static MemberUpdateResponseDto toResponseDto(MemberDto memberDto) {
        return MemberUpdateResponseDto.builder()
                .name(memberDto.getName())
                .phoneNumber(memberDto.getPhoneNumber())
                .address(memberDto.getAddress())
                .image(memberDto.getImage())
                .updatedAt(memberDto.getUpdatedAt())
                .build();
    }
}
