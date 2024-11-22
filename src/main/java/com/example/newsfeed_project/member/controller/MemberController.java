package com.example.newsfeed_project.member.controller;

import com.example.newsfeed_project.member.dto.MemberDto;
import com.example.newsfeed_project.member.dto.MemberUpdateResponseDto;
import com.example.newsfeed_project.member.dto.PasswordRequestDto;
import com.example.newsfeed_project.member.service.MemberService;
import com.example.newsfeed_project.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @GetMapping("/id")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String email = SessionUtil.validateSession(request.getSession(false));
        MemberDto memberByEmail = memberService.getMemberByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(memberByEmail);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createMember(@RequestBody MemberDto memberDto) {
        MemberDto createdMember = memberService.createMember(memberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @PutMapping("/memberUpdate")
    public ResponseEntity<?> updateMember(@RequestBody MemberDto memberDto, HttpServletRequest request) {
        String email = SessionUtil.validateSession(request.getSession(false));
        MemberDto existingMember = memberService.getMemberByEmail(email);
        MemberDto updatedMember = memberService.updateMember(existingMember.getId(), memberDto.getPassword(), memberDto);
        MemberUpdateResponseDto responseDto = MemberUpdateResponseDto.toResponseDto(updatedMember);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/email")
    public ResponseEntity<?> findByEmail(@RequestParam String email, HttpServletRequest request) {
        String sessionEmail = SessionUtil.validateSession(request.getSession(false));

        if (!sessionEmail.equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        MemberDto memberByEmail = memberService.getMemberByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(memberByEmail);
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequestDto passwordRequestDto, HttpSession session) {
        MemberDto memberDto = memberService.changePassword(passwordRequestDto.getOldPassword(), passwordRequestDto.getNewPassword(), session);
        return ResponseEntity.status(HttpStatus.OK).body(memberDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMemberById(HttpServletRequest request) {
        String email = SessionUtil.validateSession(request.getSession(false));
        MemberDto existingMember = memberService.getMemberByEmail(email);
        memberService.deleteMemberById(existingMember.getId());
        return ResponseEntity.status(HttpStatus.OK).body("회원 삭제가 완료되었습니다.");
    }
}
