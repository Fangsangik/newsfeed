package com.example.newsfeed_project.member.repository;

import com.example.newsfeed_project.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.deletedAt IS NULL")
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<Member> findById(@Param("id") Long id);

    @Query("SELECT m FROM Member m WHERE m.deletedAt IS NULL")
    List<Member> findAllActiveMembers();

    // 삭제 여부와 관계없이 이메일로 회원 조회
    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Optional<Member> findByEmailIncludingDeleted(@Param("email") String email);

    // 삭제 여부와 관계없이 이메일 존재 여부 확인
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.email = :email")
    boolean existsByEmailIncludingDeleted(@Param("email") String email);

    // 삭제되지 않은 상태에서 이메일 존재 여부 확인
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.email = :email AND m.deletedAt IS NULL")
    boolean existsByEmailAndDeletedAtIsNull(@Param("email") String email);
}
