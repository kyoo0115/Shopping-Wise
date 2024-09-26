package com.project.user.exception;

import org.springframework.http.HttpStatus;

public class VerificationCodeNotMatchedException extends GlobalException {

  public VerificationCodeNotMatchedException() {
    super(HttpStatus.BAD_REQUEST, "Verification code not matched");
  }
}