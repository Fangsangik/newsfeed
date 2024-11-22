package com.example.newsfeed_project.member.dto;

import com.example.newsfeed_project.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;

    @NotBlank(message = "이름은 필수 값 입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 값 입니다.")
    @Pattern(regexp = "[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+$", message = "이메일 형식이 일치하지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호은 필수 값 입니다.")
    @Length(min = 4, message = "비밀번호는 최소 4자입니다.")
    private String password;

    @NotBlank(message = "핸드폰 번호는 필수 값 입니다.")
    private String phoneNumber;

    @NotBlank(message = "주소는 필수 값 입니다.")
    private String address;

    @NotBlank(message = "나이는 필수 값 입니다.")
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
