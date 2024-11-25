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

import java.awt.color.CMMException;

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

    public static Comment commentToSave(CommentRequestDto dto, Newsfeed newsfeed, Member member) {
        return Comment.builder()
                .contents(dto.getContents())
                .feed(newsfeed)
                .member(member)
                .build();
    }

    public void updateComment(CommentRequestDto dto) {
        if (dto.getContents() != null) {
            this.contents = dto.getContents();
        }
    }


}
