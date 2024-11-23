package com.example.newsfeed_project.newsfeed.entity;

import com.example.newsfeed_project.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "newsfeed_like")
public class NewsfeedLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "newsfeed_id")
  private Newsfeed newsfeed;

  public void setMember(Member member) {
    this.member = member;
  }
  public void setNewsfeed(Newsfeed newsfeed) {
    this.newsfeed = newsfeed;
  }

}
