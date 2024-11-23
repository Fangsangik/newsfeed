package com.example.board.Dto;

import lombok.Getter;

@Getter
public class SignUpResponseDto {
    private final Long id;

    private final String username;

    private final Integer age;

    public SignUpResponseDto(Long id, String username, Integer age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }
}