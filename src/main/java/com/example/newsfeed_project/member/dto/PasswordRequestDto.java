package com.example.newsfeed_project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequestDto {
    private String oldPassword;
    private String newPassword;
}
