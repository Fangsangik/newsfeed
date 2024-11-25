package com.example.newsfeed_project.comment.service;

import com.example.newsfeed_project.comment.dto.CommentLikeResponseDto;
import com.example.newsfeed_project.comment.entity.Comment;
import com.example.newsfeed_project.comment.entity.CommentLike;
import com.example.newsfeed_project.comment.repository.CommentLikeRepository;
import com.example.newsfeed_project.comment.repository.CommentRepository;
import com.example.newsfeed_project.exception.NotFoundException;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_COMMENT;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final MemberService memberService;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    public CommentLikeResponseDto CommentLikeOrUnLike(Long loggedInUserId, Long commentId) {
        Member member = memberService.validateId(loggedInUserId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(NOT_FOUND_COMMENT));
        CommentLike commentLike = new CommentLike(comment, member);

        //이미 좋아요 했을 시
        CommentLike commentDelLike = commentLikeRepository.findByCommentIdAndMemberId(commentLike.getComment().getId(), commentLike.getMember().getId());
        if(commentDelLike != null){
            commentLikeRepository.delete(commentDelLike);
            return new CommentLikeResponseDto("댓글 좋아요 해제");
        }
        commentLikeRepository.save(commentLike);
        return new CommentLikeResponseDto("댓글 좋아요");
    }
}
