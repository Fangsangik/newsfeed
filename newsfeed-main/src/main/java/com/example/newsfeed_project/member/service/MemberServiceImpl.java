package com.example.newsfeed_project.member.service;

import static com.example.newsfeed_project.exception.ErrorCode.EMAIL_EXIST;
import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_EMAIL;
import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.newsfeed_project.exception.ErrorCode.SAME_PASSWORD;
import static com.example.newsfeed_project.exception.ErrorCode.WRONG_PASSWORD;

import com.example.newsfeed_project.config.PasswordEncoder;
import com.example.newsfeed_project.exception.DuplicatedException;
import com.example.newsfeed_project.exception.InvalidInputException;
import com.example.newsfeed_project.exception.NotFoundException;
import com.example.newsfeed_project.member.dto.MemberDto;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.newsfeed_project.exception.ErrorCode.*;

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
        checkIfEmailExistsIncludingDeleted(memberDto.getEmail());

        // 비밀번호 암호화 및 회원 생성
        memberDto = encryptedPassword(memberDto);
        Member newMember = Member.toEntity(memberDto);
        Member savedMember = memberRepository.save(newMember);

        return MemberDto.toDto(savedMember);
    }

    @Override
    @Transactional
    public MemberDto updateMember(Long id, String password, MemberDto memberDto) {
        Member member = validateId(id);

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidInputException(WRONG_PASSWORD);
        }

        // 회원 정보 업데이트
        member.updatedMember(memberDto);
        return MemberDto.toDto(memberRepository.save(member));
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto getMemberById(Long id) {
        Member member = validateId(id);
        return MemberDto.toDto(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto getMemberByEmail(String email) {
        Member member = validateEmail(email);
        return MemberDto.toDto(member);
    }

    @Override
    @Transactional
    public void deleteMemberById(Long id, String password) {
        Member member = memberRepository.findById(id)
                .filter(m -> !m.isDeleted()) // 이미 탈퇴된 회원은 제외
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidInputException(WRONG_PASSWORD);
        }

        member.markAsDeleted(); // 소프트 삭제
    }

    @Override
    @Transactional
    public void restoreMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));

        if (!member.isDeleted()) {
            throw new IllegalArgumentException("이미 활성화된 회원입니다.");
        }

        // 동일한 이메일을 사용하는 활성 회원 존재 여부 확인
        if (memberRepository.existsByEmailAndDeletedAtIsNull(member.getEmail())) {
            throw new IllegalArgumentException("동일한 이메일을 사용하는 활성 회원이 존재합니다.");
        }

        member.restore(); // 복구 처리
    }

    @Override
    @Transactional
    public MemberDto changePassword(String oldPassword, String newPassword, HttpSession session) {
        Member member = validateEmail(session.getAttribute("email").toString());

        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new InvalidInputException(WRONG_PASSWORD);
//            throw new IllegalArgumentException("Old password and new password do not match");
        }

        if (oldPassword.equals(newPassword)) {
            throw new InvalidInputException(SAME_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        Member changePassword = member.withPassword(encodedPassword);
        changePassword = memberRepository.save(changePassword);

        // 비밀번호 변경 후 세션 무효화
        session.invalidate();

        return MemberDto.toDto(changePassword);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String email, String password) {
        Member member = validateEmail(email);
        return passwordEncoder.matches(password, member.getPassword());
    }


    public Member validateId(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));
    }

    public Member validateEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EMAIL));
    }

    private void checkIfEmailExistsIncludingDeleted(String email) {
        if (memberRepository.existsByEmailIncludingDeleted(email)) {
            throw new DuplicatedException(EMAIL_EXIST);
        }
    }

    private MemberDto encryptedPassword(MemberDto memberDto) {
        if (memberDto.getPassword() != null && !memberDto.getPassword().isEmpty()) {
            memberDto = memberDto.withPassword(passwordEncoder.encode(memberDto.getPassword()));
        }
        return memberDto;
    }
}