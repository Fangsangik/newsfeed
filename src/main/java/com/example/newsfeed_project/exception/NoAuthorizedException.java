package com.example.newsfeed_project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoAuthorizedException extends RuntimeException {
  private final ErrorCode errorCode;
}
