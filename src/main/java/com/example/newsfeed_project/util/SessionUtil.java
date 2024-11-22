package com.example.newsfeed_project.util;

import com.example.newsfeed_project.exception.InternalServerException;
import jakarta.servlet.http.HttpSession;

import static com.example.newsfeed_project.exception.ErrorCode.SESSION_TIMEOUT;

public class SessionUtil {
        public static String validateSession(HttpSession session) {
        if (session == null || session.getAttribute("email") == null) {
            throw new InternalServerException(SESSION_TIMEOUT);
        }
        return (String) session.getAttribute("email");
    }
}
