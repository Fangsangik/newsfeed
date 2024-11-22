package com.example.newsfeed_project.member.service;

import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_EMAIL;
import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.newsfeed_project.exception.ErrorCode.SAME_PASSWORD;
import static com.example.newsfeed_project.exception.ErrorCode.WRONG_PASSWORD;

import com.example.newsfeed_project.config.PasswordEncoder;
import com.example.newsfeed_project.exception.ErrorCode;
import com.example.newsfeed_project.exception.InvalidInputException;
import com.example.newsfeed_project.exception.NotFoundException;
import com.example.newsfeed_project.member.dto.MemberDto;
import com.example.newsfeed_project.member.dto.MemberUpdateRequestDto;
import com.example.newsfeed_project.member.dto.MemberUpdateResponseDto;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public MemberDto createMember(MemberDto memberDto) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화 및 회원 생성
        memberDto = encryptedPassword(memberDto);
        Member newMember = MemberDto.toEntity(memberDto);
        Member savedMember = memberRepository.save(newMember);

        return MemberDto.toDto(savedMember);
    }

    @Override
    @Transactional
    public MemberUpdateResponseDto updateMember(MemberUpdateRequestDto requestDto) {
        // PK로 회원 조회
        Member member = memberRepository.findById(requestDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new InvalidInputException(WRONG_PASSWORD);
        }

        // 회원 정보 업데이트
        member.updatedMember(requestDto);

        // 저장 후 업데이트된 데이터 반환
        Member updatedMember = memberRepository.save(member);
        return MemberUpdateResponseDto.toResponseDto(updatedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto getMemberById(Long id) {
        Member member = validateId(id);
        return MemberDto.toDto(member);
    }

    @Override
    @Transactional
    public void deleteMemberById(Long id, String password) {
        Member member = validateId(id);

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidInputException(WRONG_PASSWORD);
        }

        member.markAsDeleted(); // 소프트 삭제
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public MemberDto changePassword(String oldPassword, String newPassword, HttpSession session) {
        // 세션에서 PK 기반으로 조회
        Long memberId = (Long) session.getAttribute("id");
        if (memberId == null) {
            throw new IllegalStateException("로그인 세션이 만료되었습니다.");
        }

        Member member = validateId(memberId);

        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new InvalidInputException(WRONG_PASSWORD);
        }

        if (oldPassword.equals(newPassword)) {
            throw new InvalidInputException(SAME_PASSWORD);
        }

        member.updatePassword(passwordEncoder.encode(newPassword));
        Member updatedMember = memberRepository.save(member);

        // 비밀번호 변경 후 세션 무효화
        session.invalidate();

        return MemberDto.toDto(updatedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public Long authenticateAndGetId(String email, String password) {
        // 이메일로 사용자를 조회
        Member member = getByMemberByEmail(email);

        // 비밀번호 검증
        if (passwordEncoder.matches(password, member.getPassword())) {
            return member.getId(); // 인증 성공 시 사용자 PK 반환
        } else {
            throw new InvalidInputException(ErrorCode.DIFFERENT_EMAIL_PASSWORD); // 예외 처리
        }
    }

    // PK 기반 멤버 검증
    public Member validateId(Long id) {
        return memberRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));
    }

    // 이메일로 멤버 조회 (제한적으로 사용)
    public Member getByMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EMAIL));
    }

    // 비밀번호 암호화
    private MemberDto encryptedPassword(MemberDto memberDto) {
        if (memberDto.getPassword() != null && !memberDto.getPassword().isEmpty()) {
            memberDto.withPassword(passwordEncoder.encode(memberDto.getPassword()));
        }
        return memberDto;
    }
}