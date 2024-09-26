package com.project.user.exception;

import org.springframework.http.HttpStatus;

public class VerificationCodeNotFoundException extends GlobalException {

  public VerificationCodeNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Verification code not found");
  }
}
