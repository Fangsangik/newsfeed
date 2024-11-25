package com.example.newsfeed_project.newsfeed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NewsfeedRequestDto {

  private String image;

  @NotBlank(message = "title은 필수 값 입니다.")
  private String title;

  @NotNull(message = "content는 필수 값 입니다.")
  private String content;
}
