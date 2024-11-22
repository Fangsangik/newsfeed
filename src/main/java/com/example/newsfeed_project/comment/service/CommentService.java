package com.example.newsfeed_project.comment.service;

import com.example.newsfeed_project.comment.dto.CommentRequestDto;
import com.example.newsfeed_project.comment.dto.CommentResponseDto;
import com.example.newsfeed_project.comment.entity.Comment;
import com.example.newsfeed_project.comment.repository.CommentLikeRepository;
import com.example.newsfeed_project.comment.repository.CommentRepository;
import com.example.newsfeed_project.exception.NoAuthorizedException;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.service.MemberService;
import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import com.example.newsfeed_project.newsfeed.repository.NewsfeedRepository;
import com.example.newsfeed_project.newsfeed.service.NewsfeedService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.newsfeed_project.exception.ErrorCode.NO_AUTHOR_CHANGE;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final NewsfeedService newsfeedService;
    private final NewsfeedRepository newsfeedRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentService(CommentRepository commentRepository, MemberService memberService, NewsfeedService newsfeedService, NewsfeedRepository newsfeedRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.memberService = memberService;
        this.newsfeedService = newsfeedService;
        this.newsfeedRepository = newsfeedRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    //댓글 생성
    public CommentResponseDto createComment(Long newsfeedId, CommentRequestDto dto, String email) {
        Newsfeed newsfeed = newsfeedService.findNewsfeedByIdOrElseThrow(newsfeedId);
        Member member = memberService.validateEmail(email);
        Comment comment = Comment.toEntity(dto);
        comment.setMember(member);
        comment.setNewsFeed(newsfeed);
        Comment save = commentRepository.save(comment);

        return CommentResponseDto.toDto(save);
    }

    //댓글 전체 조회
    public List<CommentResponseDto> findAll(Long newsfeedId, Pageable pageable) {
        return commentRepository.findByFeedId(newsfeedId, pageable).stream().map(CommentResponseDto::toDto).toList();
    }

    //댓글 단건 조회
    public CommentResponseDto findById(Long id) {
        //댓글 존재 여부 확인
        Comment findId = findCommentByIdOrElseThrow(id);

        return new CommentResponseDto(findId.getContents(), findId.getCreatedAt());
    }

    //댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto dto, String email) {
        //댓글 존재 여부 확인
        findCommentByIdOrElseThrow(commentId);

        Member member = memberService.validateEmail(email);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("뉴스피드를 찾지 못했습니다."));
        Newsfeed newsfeed = newsfeedRepository.findById(comment.getFeed().getId()).orElseThrow(() -> new IllegalArgumentException("뉴스피드를 찾지 못했습니다."));

        //사용자 검증
        checkCommentAuthorOrNewsfeedAuthor(email, comment, newsfeed);

        comment.updateComment(dto);
        Comment save = commentRepository.save(comment);

        return CommentResponseDto.toDto(save);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String email) {
        //댓글 존재 여부 확인
        findCommentByIdOrElseThrow(commentId);

        Member member = memberService.validateEmail(email);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글을 찾지 못했습니다."));
        Newsfeed newsfeed = newsfeedRepository.findById(comment.getFeed().getId()).orElseThrow(() -> new IllegalArgumentException("뉴스피드를 찾지 못했습니다."));

        //사용자 검증
        checkCommentAuthorOrNewsfeedAuthor(email, comment, newsfeed);

        if(commentLikeRepository.findByCommentId(commentId) != null) {
            commentLikeRepository.deleteByCommentId(commentId);
        }
        commentRepository.deleteById(commentId);
    }

    public Comment findCommentByIdOrElseThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 찾지 못했습니다."));
    }

    //댓글 수정,삭제 시 댓글 작성자 or 게시글 작성자만 가능
    private void checkCommentAuthorOrNewsfeedAuthor(String email, Comment comment, Newsfeed newsfeed) {
        if (newsfeed != null && comment != null) {
            if(!comment.getMember().getEmail().equals(email)) {
                if (!newsfeed.getMember().getEmail().equals(email)) {
                    throw new NoAuthorizedException(NO_AUTHOR_CHANGE);
                }
            }
        } else if (newsfeed != null && comment == null) {
            if (!newsfeed.getMember().getEmail().equals(email)) {
                throw new NoAuthorizedException(NO_AUTHOR_CHANGE);
            }
        } else if (newsfeed == null && comment != null) {
            if (!comment.getMember().getEmail().equals(email)) {
                throw new NoAuthorizedException(NO_AUTHOR_CHANGE);
            }
        }else {
            throw new NoAuthorizedException(NO_AUTHOR_CHANGE);
        }
    }
}
