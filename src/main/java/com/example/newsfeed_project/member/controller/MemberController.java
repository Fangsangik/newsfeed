package com.example.newsfeed_project.member.controller;

import com.example.newsfeed_project.member.dto.*;
import com.example.newsfeed_project.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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


    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        MemberDto getMemberId = memberService.getMemberById(id);
        return ResponseEntity.status(HttpStatus.OK).body(getMemberId);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberDto memberDto, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("id");
        MemberDto createdMember = memberService.createMember(memberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @PutMapping("/update")
    public ResponseEntity<MemberUpdateResponseDto> updateMember(
            @Valid @RequestBody MemberUpdateRequestDto requestDto,
            HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("id");

        // 업데이트 서비스 호출
        MemberUpdateResponseDto responseDto = memberService.updateMember(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordRequestDto passwordRequestDto, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("id");
        MemberDto memberDto = memberService.changePassword(passwordRequestDto.getOldPassword(), passwordRequestDto.getNewPassword(), session);
        return ResponseEntity.status(HttpStatus.OK).body(memberDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMemberById(@Valid @RequestBody DeleteRequestDto deleteRequestDto, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("id");
        // 회원 탈퇴 처리 (비밀번호 검증 포함)
        memberService.deleteMemberById(deleteRequestDto.getId(), deleteRequestDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body("회원 삭제가 완료되었습니다.");
    }
}
