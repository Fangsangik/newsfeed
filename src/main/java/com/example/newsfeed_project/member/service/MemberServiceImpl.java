package com.example.newsfeed_project.member.service;

import com.example.newsfeed_project.config.PasswordEncoder;
import com.example.newsfeed_project.member.dto.MemberDto;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.OptimisticLockingFailureException;
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
        if (memberRepository.findByEmail(memberDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        memberDto = encryptedPassword(memberDto);
        Member createMember = Member.toEntity(memberDto);
        Member save = memberRepository.save(createMember);
        return MemberDto.toDto(save);
    }

    @Override
    @Transactional
    public MemberDto updateMember(Long id, String password, MemberDto memberDto) {
        Member findMemberId = validateId(id);
        if (!passwordEncoder.matches(password, findMemberId.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        findMemberId.updatedMember(memberDto);

        try {
            Member updatedMember = memberRepository.save(findMemberId);
            return MemberDto.toDto(updatedMember);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("충돌이 발생했습니다. 다시 시도하세요");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto getMemberById(Long id) {
        Member findMemberId = validateId(id);
        return MemberDto.toDto(findMemberId);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto getMemberByEmail(String email) {
        Member member = validateEmail(email);

        return MemberDto.toDto(member);
    }

    @Override
    @Transactional
    public void deleteMemberById(Long id) {
        validateId(id);
        memberRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MemberDto changePassword(String oldPassword, String newPassword, HttpSession session) {
        Member member = validateEmail(session.getAttribute("email").toString());

        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new IllegalArgumentException("Old password and new password do not match");
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

        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            return true;
        }
        return false;
    }


    public Member validateId(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    public Member validateEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member email not found"));
        return member;
    }

    private MemberDto encryptedPassword(MemberDto memberDto) {
        if (memberDto.getPassword() != null && !memberDto.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(memberDto.getPassword());
            memberDto = memberDto.withPassword(encodedPassword);
        }
        return memberDto;
    }
}
