package com.example.newsfeed_project.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Slf4j
public class LoginFilter implements Filter {

    private static final String[] whiteList = {"/api/login", "/api/logout", "/members/register"};
    private static final String SESSION_ID_KEY = "id";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

        try {
            log.info("인증 체크 시작 - URI: {}, Method: {}, IP: {}",
                    requestURI, request.getMethod(), request.getRemoteAddr());

            // /members/{id} 경로 처리
            if (requestURI.matches("/members/\\d+")) {
                log.info("/members/{id} 경로 요청 - URI: {}", requestURI);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            // 화이트리스트 체크
            if (isWhiteListPath(requestURI)) {
                log.info("화이트리스트 경로 요청 - URI: {}", requestURI);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 - URI: {}", requestURI);

                HttpSession session = request.getSession(false);

                if (session == null || session.getAttribute(SESSION_ID_KEY) == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다");
                    log.info("미인증 사용자 요청 - URI: {}", requestURI);
                    return;
                }
            }


            // 필터 체인 계속 진행
            filterChain.doFilter(request, response);

        } catch (IOException | ServletException e) {
            log.error("필터 처리 중 예외 발생 - URI: {}, Error: {}", requestURI, e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다.");
        }
    }

    // 화이트리스트 경로인지 확인
    private boolean isWhiteListPath(String requestURI) {
        return PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }

    // 인증 체크가 필요한 경로인지 확인
    private boolean isLoginCheckPath(String requestURI) {
        // 화이트리스트가 아니면 인증 체크 필요
        return !isWhiteListPath(requestURI);
    }
}