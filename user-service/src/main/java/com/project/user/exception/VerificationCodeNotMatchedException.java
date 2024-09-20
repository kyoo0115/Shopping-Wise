package com.project.user.exception;

public class VerificationCodeNotMatchedException extends RuntimeException {

  public VerificationCodeNotMatchedException(String message) {
    super(message);
  }
}