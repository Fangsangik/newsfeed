package com.example.newsfeed_project.newsfeed.service;

import com.example.newsfeed_project.newsfeed.dto.NewsfeedRequestDto;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedResponseDto;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedTermRequestDto;
import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface NewsfeedService {

  //뉴스피드 생성
  NewsfeedResponseDto save(NewsfeedRequestDto dto, Long loggedInUserId);

  NewsfeedResponseDto updateNewsfeed(Long id, NewsfeedRequestDto dto, Long loggedInUserId);

  void delete(Long id, Long loggedInUserId);

  Newsfeed findNewsfeedByIdOrElseThrow(Long id);

  List<NewsfeedResponseDto> findNewsfeed(boolean isLike, Long memberId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}