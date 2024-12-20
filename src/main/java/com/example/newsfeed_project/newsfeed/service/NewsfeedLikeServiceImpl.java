package com.example.newsfeed_project.newsfeed.service;

import static com.example.newsfeed_project.exception.ErrorCode.ALREADY_LIKED;
import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_NEWSFEED_LIKE;
import static com.example.newsfeed_project.exception.ErrorCode.NO_SELF_LIKE;

import com.example.newsfeed_project.exception.DuplicatedException;
import com.example.newsfeed_project.exception.NoAuthorizedException;
import com.example.newsfeed_project.exception.NotFoundException;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.service.MemberService;
import com.example.newsfeed_project.newsfeed.dto.LikeResponseDto;
import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import com.example.newsfeed_project.newsfeed.entity.NewsfeedLike;
import com.example.newsfeed_project.newsfeed.repository.NewsfeedLikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsfeedLikeServiceImpl implements NewsfeedLikeService {

  private final NewsfeedService newsfeedService;
  private final MemberService memberService;
  private final NewsfeedLikeRepository newsfeedLikeRepository;

  @Transactional
  @Override
  public LikeResponseDto addLike(Long loggedInUserId, long newsfeedId) {
    NewsfeedLike newsfeedLike = checkLike(loggedInUserId, newsfeedId);
    if(newsfeedLike.getId() != null) {
      newsfeedLikeRepository.delete(newsfeedLike);
      newsfeedLike.getNewsfeed().setLikeCount(newsfeedLike.getNewsfeed().getLikeCount() - 1);
      return new LikeResponseDto("좋아요 해제");
    }else{
      newsfeedLikeRepository.save(newsfeedLike);
      newsfeedLike.getNewsfeed().setLikeCount(newsfeedLike.getNewsfeed().getLikeCount() + 1);
      return new LikeResponseDto("좋아요 설정");
    }
  }

  private NewsfeedLike checkLike(Long loggedInUserId, long newsfeedId) {
    Member member = memberService.validateId(loggedInUserId);
    Newsfeed newsfeed = newsfeedService.findNewsfeedByIdOrElseThrow(newsfeedId);
    checkAuthor(newsfeed, loggedInUserId);
    NewsfeedLike newsfeedLike = newsfeedLikeRepository.findByNewsfeedIdAndMemberId(newsfeedId, member.getId());
    if(newsfeedLike == null){
      newsfeedLike = new NewsfeedLike(member, newsfeed);
    }
    return newsfeedLike;
  }

  private void checkAuthor(Newsfeed newsfeed, Long loggedInUserId){
    if(newsfeed.getMember().getId().equals(loggedInUserId)){
      throw new NoAuthorizedException(NO_SELF_LIKE);
    }
  }
}
