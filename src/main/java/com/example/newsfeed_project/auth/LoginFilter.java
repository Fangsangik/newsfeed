package com.example.newsfeed_project.auth;

import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.service.MemberService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Slf4j
public class LoginFilter implements Filter {

    private static final String[] whiteList = {"/api/login", "/api/logout", "/members/register", "/members/{id}"};

    private final MemberService memberService;

    public LoginFilter(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            log.info("인증 체크 시작 : {}", requestURI);

            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 : {}", requestURI);

                HttpSession session = request.getSession(false);

                // 세션이 없거나 email 정보가 없다면 인증되지 않은 상태
                if (session == null || session.getAttribute("email") == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다");
                    log.info("미인증 사용자 요청 : {}", requestURI);
                    return;
                }

                // 세션에서 이메일 가져오기
                String sessionEmail = (String) session.getAttribute("email");

                // DB나 서비스에서 사용자를 조회
                Member member = Member.toEntity(memberService.getMemberByEmail(sessionEmail));

                // 세션 이메일이 유효한 사용자에 해당하는지 확인
                if (member == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "세션의 이메일에 해당하는 사용자가 존재하지 않습니다");
                    log.info("세션 이메일에 해당하는 사용자가 없음 : {}", requestURI);
                    return;
                }

                // 요청 본문에서 이메일을 가져와서 비교
                String requestEmail = request.getParameter("email");

                // 요청 이메일과 세션의 이메일이 일치하는지 검증
                if (requestEmail != null && !sessionEmail.equals(requestEmail)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "요청된 이메일이 세션과 일치하지 않습니다");
                    log.info("클라이언트의 이메일 정보가 세션과 불일치 : {}", requestURI);
                    return;
                }
            }
            // 필터 체인 계속 진행
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            log.error("필터 처리 중 예외 발생 : {}", e.getMessage(), e);
            throw e;  // 예외를 다시 던져서 Spring에 전달
        }
    }

    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}