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

  private long likeCount;

  private LocalDateTime updatedAt;

  public static NewsfeedResponseDto toDto(Newsfeed newsfeed) {
    String email = "";
    if(newsfeed.getMember().getDeletedAt() == null) {
      email = newsfeed.getMember().getEmail();
    }else{
      email = "삭제된 사용자";
    }
    return new NewsfeedResponseDto(
        newsfeed.getFeedImage(),
        newsfeed.getTitle(),
        newsfeed.getContent(),
        email,
        newsfeed.getLikeCount(),
        newsfeed.getUpdatedAt()
    );
  }
}
