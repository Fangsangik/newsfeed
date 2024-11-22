package com.example.newsfeed_project.newsfeed.repository;

import com.example.newsfeed_project.newsfeed.entity.Newsfeed;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long> {

  List<Newsfeed> findByMemberId(long memberId, Pageable pageable);

  List<Newsfeed> findByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);
//  List<Newsfeed> findBetweenCreatedAt(LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

  List<Newsfeed> findByMemberIdBetween(Long memberId, LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);
}
