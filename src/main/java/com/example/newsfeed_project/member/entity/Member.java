package com.example.newsfeed_project.member.entity;

import com.example.newsfeed_project.member.dto.MemberDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private int age;
    private String image;

    private LocalDateTime deletedAt;

    @Version
    //낙관적 락
    //버전 관리를 통해 동시성 충돌 감지.
    private Integer version;

    public static Member toEntity(MemberDto memberDtO) {
        return Member.builder()
                .name(memberDtO.getName())
                .email(memberDtO.getEmail())
                .password(memberDtO.getPassword())
                .phoneNumber(memberDtO.getPhoneNumber())
                .address(memberDtO.getAddress())
                .age(memberDtO.getAge())
                .image(memberDtO.getImage())
                .deletedAt(memberDtO.getDeletedAt())
                .build();
    }

    public void updatedMember(MemberDto memberDtO) {
        if (memberDtO.getName() != null) {
            this.name = memberDtO.getName();
        }

        if (memberDtO.getImage() != null) {
            this.image = memberDtO.getImage();
        }

        if (memberDtO.getPhoneNumber() != null) {
            this.phoneNumber = memberDtO.getPhoneNumber();
        }

        if (memberDtO.getAddress() != null) {
            this.address = memberDtO.getAddress();
        }
    }

    public Member withPassword(String password) {
        return Member.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .password(password) // 변경된 비밀번호
                .phoneNumber(this.phoneNumber)
                .address(this.address)
                .age(this.age)
                .image(this.image)
                .deletedAt(this.deletedAt)
                .version(this.version)
                .build();
    }
}

