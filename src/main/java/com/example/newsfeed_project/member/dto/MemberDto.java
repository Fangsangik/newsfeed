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

    public MemberDto withPassword(String password) {
        return MemberDto.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .password(password)
                .phoneNumber(this.phoneNumber)
                .address(this.address)
                .age(this.age)
                .image(this.image)
                .updatedAt(this.updatedAt)
                .deletedAt(this.deletedAt)
                .createdAt(this.createdAt)
                .build();
    }
}
