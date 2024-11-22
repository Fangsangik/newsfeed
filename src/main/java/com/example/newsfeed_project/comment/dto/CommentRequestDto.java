package com.example.newsfeed_project.comment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @Valid
    @NotBlank(message = "댓글은 필수사항입니다.")
    private String contents;
}
