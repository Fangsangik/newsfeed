package com.example.newsfeed_project.comment.controller;

import com.example.newsfeed_project.comment.dto.CommentLikeResponseDto;
import com.example.newsfeed_project.comment.service.CommentLikeService;
import com.example.newsfeed_project.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    // 좋아요 및 좋아요해제
    @PostMapping(value = "/comment/{commentId}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> commentLikeAndDelLike(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        String email = SessionUtil.validateSession(request.getSession());
        CommentLikeResponseDto dto = commentLikeService.commentLikeorDelLike(email, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
