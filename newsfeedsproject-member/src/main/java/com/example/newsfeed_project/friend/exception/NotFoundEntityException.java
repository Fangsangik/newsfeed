package com.example.newsfeed_project.friend.exception;

import com.example.newsfeed_project.friend.exception.ExceptionCode;
import lombok.Getter;

@Getter
public class NotFoundEntityException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    public NotFoundEntityException(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
