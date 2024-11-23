package com.example.newsfeed_project.newsfeed.service;

import com.example.newsfeed_project.newsfeed.dto.NewsfeedRequestDto;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedResponseDto;
import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsfeedService {

  //뉴스피드 생성
  NewsfeedResponseDto save(NewsfeedRequestDto dto, String email);

  List<NewsfeedResponseDto> findAll(Pageable pageable);

  NewsfeedResponseDto updateNewsfeed(Long id, NewsfeedRequestDto dto, String email);

  void delete(Long id, String email);

  Newsfeed findNewsfeedByIdOrElseThrow(Long id);

  List<NewsfeedResponseDto> findAllOrderByLikes(Pageable pageable);

  List<NewsfeedResponseDto> findByMemberId(long memberId, Pageable pageable);
}
