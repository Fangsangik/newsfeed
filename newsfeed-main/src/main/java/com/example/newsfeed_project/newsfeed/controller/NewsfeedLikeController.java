package com.example.newsfeed_project.newsfeed.controller;

import com.example.newsfeed_project.newsfeed.dto.LikeResponseDto;
import com.example.newsfeed_project.newsfeed.service.NewsfeedLikeService;
import com.example.newsfeed_project.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/newsfeed")
@RequiredArgsConstructor
public class NewsfeedLikeController {

  private final NewsfeedLikeService newsfeedLikeService;

  //뉴스피드에서 좋아요를 설정 / 해제
  @PatchMapping("/{newsfeedId}/like")
  public ResponseEntity<LikeResponseDto> addLike(
      @PathVariable long newsfeedId,
      HttpServletRequest request
  ){
    String email = SessionUtil.validateSession(request.getSession());
    LikeResponseDto likeResPonseDto = newsfeedLikeService.addLike(email, newsfeedId);
    return new ResponseEntity<>(likeResPonseDto, HttpStatus.OK);
  }
}
