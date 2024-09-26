package com.project.user.exception;

import org.springframework.http.HttpStatus;

public class VerificationCodeExpiredException extends GlobalException {

  public VerificationCodeExpiredException() {
    super(HttpStatus.CONFLICT, "Verification code expired");
  }
}
