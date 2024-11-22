package com.example.newsfeed_project.member.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteRequestDto {
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String oldPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
