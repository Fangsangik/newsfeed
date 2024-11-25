package com.example.board.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.board.Entity.Member; // Member 클래스 패키지 경로도 확인

public interface MemberRepository extends JpaRepository<Member, Long> {
}