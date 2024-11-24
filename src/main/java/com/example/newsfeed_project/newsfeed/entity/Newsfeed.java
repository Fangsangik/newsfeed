package com.example.newsfeed_project.newsfeed.entity;

import com.example.newsfeed_project.common.BaseEntity;
import com.example.newsfeed_project.member.dto.MemberDto;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "newsfeed")
public class Newsfeed extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  private String feedImage;

  private String title;

  private String content;

  private long likeCount;

  public void setLikeCount(long likeCount) {
    this.likeCount = likeCount;
  }

  public Newsfeed(Member member, String feedImage, String title, String content) {
    this.member = member;
    this.feedImage = feedImage;
    this.title = title;
    this.content = content;
    this.likeCount = 0;
  }

  public void updateNewsfeed(NewsfeedRequestDto newsfeed) {
    this.feedImage = newsfeed.getImage();
    this.title = newsfeed.getTitle();
    this.content = newsfeed.getContent();
  }

}
