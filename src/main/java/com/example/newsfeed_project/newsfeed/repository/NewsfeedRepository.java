package com.example.newsfeed_project.newsfeed.repository;

import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long> {

  List<Newsfeed> findByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

  List<Newsfeed> findByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);
  Page<Newsfeed> findByMemberIdIn(List<Long> friendIds, Pageable pageable);
  @Query("select n "
      + "from Newsfeed n "
      + "where (:memberId is null or n.member.id = :memberId) "
      + "and n.createdAt between :startDate and :endDate")
  List<Newsfeed> findNewsfeed(Long memberId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
