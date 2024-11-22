package com.example.newsfeed_project.member.dto;

import com.example.newsfeed_project.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;


    // @NotBlank(message = "나이는 필수 값 입니다.")

    private int age;
    private String image;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;

    // Entity에서 DTO로 변환하는 생성자
    public static MemberDto toDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .age(member.getAge())
                .image(member.getImage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(member.getDeletedAt() != null ? member.getDeletedAt() : null)
                .build();
    }

    //update
    public void withPassword(String password) {
        this.password = password;
    }

    public static Member toEntity(MemberDto memberDto) {
        return Member.builder()
                .name(memberDto.getName())
                .email(memberDto.getEmail())
                .password(memberDto.getPassword())
                .phoneNumber(memberDto.getPhoneNumber())
                .address(memberDto.getAddress())
                .age(memberDto.getAge())
                .image(memberDto.getImage())
                .deletedAt(memberDto.getDeletedAt())
                .build();
    }
}
