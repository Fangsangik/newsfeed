package com.example.newsfeed_project.comment.controller;

import com.example.newsfeed_project.comment.dto.CommentLikeResponseDto;
import com.example.newsfeed_project.comment.service.CommentLikeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    // 좋아요 및 좋아요 해제
//    @PostMapping(value = "/comment/{commentId}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
    @PatchMapping("/comments/{commentId}/likes")
    public ResponseEntity<?> commentLikeAndDelLike(
            @PathVariable Long commentId,
            HttpSession session
    ) {
        Long loggedInUserId = (Long) session.getAttribute("id");
        CommentLikeResponseDto dto = commentLikeService.CommentLikeOrUnLike(loggedInUserId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
