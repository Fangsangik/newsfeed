package com.example.newsfeed_project.comment.repository;

import com.example.newsfeed_project.comment.entity.Comment;
import com.example.newsfeed_project.comment.entity.CommentLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByFeedId(Long newsfeedId, Pageable pageable);

    Comment findByMemberId(Long id);
}
