package com.example.newsfeed_project.newsfeed.repository;

import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import com.example.newsfeed_project.newsfeed.entity.NewsfeedLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsfeedLikeRepository extends JpaRepository<NewsfeedLike, Long> {

  NewsfeedLike findByNewsfeedIdAndMemberId( Long newsfeedId, Long memberId);

  long countByNewsfeedId(Long id);

  void deleteByNewsfeedId(Long id);

  List<NewsfeedLike> findByNewsfeedId(long newsfeedId);
}
