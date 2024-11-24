package com.example.newsfeed_project.newsfeed.entity;

import com.example.newsfeed_project.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "newsfeed_like")
@NoArgsConstructor
@AllArgsConstructor
public class NewsfeedLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "newsfeed_id")
  private Newsfeed newsfeed;

  public NewsfeedLike(Member member, Newsfeed newsfeed) {
    this.member = member;
    this.newsfeed = newsfeed;
  }
}
