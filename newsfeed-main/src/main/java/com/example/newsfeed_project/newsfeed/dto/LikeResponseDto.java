package com.example.newsfeed_project.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {

  // 좋아요의 생성 / 취소를 전해주는 메서드
  private String message;

}
