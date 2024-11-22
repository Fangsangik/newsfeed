package com.example.newsfeed_project.member.controller;

import static com.example.newsfeed_project.exception.ErrorCode.DIFFERENT_EMAIL_PASSWORD;
import static com.example.newsfeed_project.exception.ErrorCode.NO_SESSION;
import com.example.newsfeed_project.exception.InvalidInputException;
import com.example.newsfeed_project.exception.NoAuthorizedException;
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

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto,
                                   HttpServletRequest request) {
        // 이메일과 비밀번호를 검증
        Long userId = memberService.authenticateAndGetId(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        log.debug("authenticateAndGetId 결과: {}", userId);
        if (userId != null) {
            // 기존 세션 무효화
            HttpSession existingSession = request.getSession(false);
            if (existingSession != null) {
                existingSession.invalidate();
            }

            // 새로운 세션 생성
            HttpSession session = request.getSession(true);
            session.setAttribute("id", userId);  // 세션에 사용자 PK 저장
            log.info("로그인 성공 : User ID {}", userId);
            return ResponseEntity.status(HttpStatus.OK).body("로그인 성공");
        } else {
            log.info("로그인 실패 : {}", loginRequestDto.getEmail());
            throw new InvalidInputException(DIFFERENT_EMAIL_PASSWORD);
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("로그아웃 성공");
        } else {
            log.info("세션 없음 : 로그아웃 처리됨");
        }
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
    }
}