package com.example.newsfeed_project.comment.entity;

import com.example.newsfeed_project.comment.dto.CommentRequestDto;
import com.example.newsfeed_project.comment.dto.CommentResponseDto;
import com.example.newsfeed_project.common.BaseEntity;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "newsfeed_id")
    private Newsfeed feed;

    private String contents;

    public static Comment toEntity(CommentRequestDto dto) {
        return Comment.builder()
                .contents(dto.getContents())
                .build();
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setNewsFeed(Newsfeed feed) {
        this.feed = feed;
    }

    public void updateComment(CommentRequestDto dto) {
        if (dto.getContents() != null) {
            this.contents = dto.getContents();
        }
    }


}
