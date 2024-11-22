package com.example.newsfeed_project.comment.dto;

import com.example.newsfeed_project.comment.entity.Comment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class CommentResponseDto {

    @Valid
    @NotBlank(message = "댓글은 필수사항입니다.")
    private  String contents;
    private  LocalDateTime updatedAt;

    public static CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .contents(comment.getContents())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}