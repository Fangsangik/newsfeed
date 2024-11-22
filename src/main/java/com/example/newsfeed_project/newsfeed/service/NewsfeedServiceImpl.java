package com.example.newsfeed_project.newsfeed.service;

import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_NEWSFEED;
import static com.example.newsfeed_project.exception.ErrorCode.NO_AUTHOR_CHANGE;
import static java.util.Objects.isNull;

import com.example.newsfeed_project.exception.NoAuthorizedException;
import com.example.newsfeed_project.exception.NotFoundException;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.service.MemberService;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedRequestDto;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedResponseDto;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedTermRequestDto;
import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import com.example.newsfeed_project.newsfeed.entity.NewsfeedLike;
import com.example.newsfeed_project.newsfeed.repository.NewsfeedLikeRepository;
import com.example.newsfeed_project.newsfeed.repository.NewsfeedRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.hibernate.dialect.function.array.JsonArrayViaElementArgumentReturnTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsfeedServiceImpl implements NewsfeedService{

  private final NewsfeedRepository newsfeedRepository;
  private final MemberService memberService;
  private final NewsfeedLikeRepository newsfeedLikeRepository;

  @Override
  public NewsfeedResponseDto save(NewsfeedRequestDto dto, String email) {
    Member member = memberService.getByMemberByEmail(email);
    Newsfeed newsfeed = new Newsfeed(member, dto.getImage(), dto.getTitle(), dto.getContent());
    newsfeedRepository.save(newsfeed);
    long like = newsfeedLikeRepository.countByNewsfeedId(newsfeed.getId());
    return new NewsfeedResponseDto(newsfeed.getFeedImage(), newsfeed.getTitle(), newsfeed.getContent(), newsfeed.getMember().getEmail(), like , newsfeed.getUpdatedAt());
  }

  @Override
  public List<NewsfeedResponseDto> findNewsfeed(boolean isLike, Long memberId,
      LocalDate startDate, LocalDate endDate, Pageable pageable) {
    pageable = checkSortedByLike(isLike, pageable);
    if(isNull(memberId)) {
      return newsfeedRepository.findByCreatedAtBetween(startDate.atStartOfDay(),
              endDate.atTime(LocalTime.MAX), pageable)
          .stream()
          .map(NewsfeedResponseDto::toDto)
          .toList();
    }else{
      return newsfeedRepository.findByMemberIdAndCreatedAtBetween(memberId, startDate.atStartOfDay(),
              endDate.atTime(LocalTime.MAX), pageable)
          .stream()
          .map(NewsfeedResponseDto::toDto)
          .toList();
    }
  }

  @Transactional
  @Override
  public NewsfeedResponseDto updateNewsfeed(Long id, NewsfeedRequestDto dto, String email) {
    Newsfeed newsfeed = findNewsfeedByIdOrElseThrow(id);
    checkEmail(email, newsfeed);
    newsfeed.updateNewsfeed(dto);
    long like = newsfeedLikeRepository.countByNewsfeedId(newsfeed.getId());
    return new NewsfeedResponseDto(newsfeed.getFeedImage(), newsfeed.getTitle(), newsfeed.getContent(), newsfeed.getMember().getEmail(), like, newsfeed.getUpdatedAt());
  }

  @Transactional
  @Override
  public void delete(Long id, String email) {
    Newsfeed newsfeed = findNewsfeedByIdOrElseThrow(id);
    checkEmail(email, newsfeed);
    deleteNewsfeedLike(id);
    newsfeedRepository.delete(newsfeed);
  }

  @Override
  public Newsfeed findNewsfeedByIdOrElseThrow(Long id) {
    return newsfeedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(NOT_FOUND_NEWSFEED));
  }

  private void checkEmail(String email, Newsfeed newsfeed) {
    if(!newsfeed.getMember().getEmail().equals(email)) {
      throw new NoAuthorizedException(NO_AUTHOR_CHANGE);
    }
  }

  private void deleteNewsfeedLike(long newsfeedId) {
    List<NewsfeedLike> newsfeedLike = newsfeedLikeRepository.findByNewsfeedId(newsfeedId);
    if(!newsfeedLike.isEmpty()) {
      newsfeedLikeRepository.deleteByNewsfeedId(newsfeedId);
    }
  }

  private Pageable checkSortedByLike(boolean isLike, Pageable pageable ) {
    if (isLike) {
      pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
          Sort.by("likeCount").descending().and(Sort.by("updatedAt").descending()));
    }
    return pageable;
  }

}
