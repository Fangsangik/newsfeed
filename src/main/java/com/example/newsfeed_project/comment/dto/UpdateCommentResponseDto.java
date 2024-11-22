package com.example.newsfeed_project.comment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentResponseDto {
    @Valid
    @NotBlank(message = "댓글은 필수사항입니다.")
    private String contents;
    private LocalDateTime updatedAt;

    public static UpdateCommentResponseDto toResponseDto(CommentResponseDto commentResponseDto) {
        return UpdateCommentResponseDto.builder()
                .contents(commentResponseDto.getContents())
                .updatedAt(commentResponseDto.getUpdatedAt())
                .build();
    }
}
