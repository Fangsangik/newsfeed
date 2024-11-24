package com.example.newsfeed_project.member.entity;

import com.example.newsfeed_project.common.BaseEntity;
import com.example.newsfeed_project.member.dto.MemberDto;
import com.example.newsfeed_project.member.dto.MemberUpdateRequestDto;
import com.example.newsfeed_project.member.dto.MemberUpdateResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member extends BaseEntity {

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

    //낙관적 락
    //버전 관리를 통해 동시성 충돌 감지.
    @Version
    private Integer version;


    public void updatedMember(MemberUpdateRequestDto updatedDto) {
        if (updatedDto.getName() != null) {
            this.name = updatedDto.getName();
        }

        if (updatedDto.getImage() != null) {
            this.image = updatedDto.getImage();
        }

        if (updatedDto.getPhoneNumber() != null) {
            this.phoneNumber = updatedDto.getPhoneNumber();
        }

        if (updatedDto.getAddress() != null) {
            this.address = updatedDto.getAddress();
        }
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}

