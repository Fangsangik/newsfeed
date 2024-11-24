package com.example.newsfeed_project.member.service;

import com.example.newsfeed_project.member.dto.MemberDto;
import com.example.newsfeed_project.member.dto.MemberUpdateRequestDto;
import com.example.newsfeed_project.member.dto.MemberUpdateResponseDto;
import com.example.newsfeed_project.member.entity.Member;
import jakarta.servlet.http.HttpSession;

public interface MemberService {
    MemberDto createMember(MemberDto memberDto);
    MemberUpdateResponseDto updateMember(MemberUpdateRequestDto requestDto);
    MemberDto getMemberById(Long id);
    void deleteMemberById(Long id, String password);
    MemberDto changePassword(String oldPassword, String newPassword, HttpSession session);
    Member validateId(Long id);
    Member getByMemberByEmail(String email);
    Long authenticateAndGetId(String email, String password);
}
