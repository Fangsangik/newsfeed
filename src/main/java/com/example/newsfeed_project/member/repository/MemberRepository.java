package com.example.newsfeed_project.member.repository;

import com.example.newsfeed_project.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // PK와 삭제되지 않은 조건으로 멤버 조회
    @Query("SELECT m FROM Member m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<Member> findByIdAndNotDeleted(@Param("id") Long id);

    // 이메일 중복 여부 확인
    //@Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.email = :email AND m.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);

    // 이메일로 멤버 조회
    //@Query("SELECT m FROM Member m WHERE m.email = :email AND m.deletedAt IS NULL")
    Optional<Member> findByEmail(@Param("email") String email);
}
