package com.example.newsfeed_project.newsfeed.service;

import com.example.newsfeed_project.newsfeed.dto.LikeResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface NewsfeedLikeService {

  LikeResponseDto addLike(String email, long newsfeedId);
}
