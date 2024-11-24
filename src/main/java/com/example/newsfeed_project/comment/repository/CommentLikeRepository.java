package com.example.newsfeed_project.comment.repository;

import com.example.newsfeed_project.comment.entity.CommentLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByCommentIdAndMemberId(Long commentId, Long memberId);
    CommentLike findByCommentId(Long commentID);
    void deleteByCommentId(Long id);
    void deleteByCommentIdIn(List<Long> commentId);
}
