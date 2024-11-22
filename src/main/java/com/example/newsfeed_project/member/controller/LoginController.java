package com.example.newsfeed_project.member.controller;

import com.example.newsfeed_project.member.dto.LoginRequestDto;
import com.example.newsfeed_project.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {

    private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequestDto loginRequestDto,
                                    HttpServletRequest request) {
        boolean authenticate = memberService.authenticate(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        if (authenticate) {
            HttpSession session = request.getSession(true);
            session.setAttribute("email", loginRequestDto.getEmail());
            log.info("logged in successfully : {}", loginRequestDto.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body("로그인 성공");
        } else {
            log.info("로그인 실패 : {}", loginRequestDto.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호 일치하지 않습니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("logged out successfully");
            return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
        } else {
            log.info("세션 없음 : 로그아웃 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현제 로그인 중인게 없습니다.");
        }
    }
}
