package com.example.newsfeed_project.newsfeed.dto;

import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewsfeedResponseDto {

  private String feedImage;

  private String title;

  private String content;

  private String author;

  private long like;

  private LocalDateTime updatedAt;

  public static NewsfeedResponseDto toDto(Newsfeed newsfeed, long like) {
    return new NewsfeedResponseDto(
        newsfeed.getFeedImage(),
        newsfeed.getTitle(),
        newsfeed.getContent(),
        newsfeed.getMember().getEmail(),
        like,
        newsfeed.getUpdatedAt()
    );
  }
}
