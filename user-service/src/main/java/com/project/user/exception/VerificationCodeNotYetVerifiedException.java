package com.project.user.exception;

import org.springframework.http.HttpStatus;

public class VerificationCodeNotYetVerifiedException extends GlobalException {

  public VerificationCodeNotYetVerifiedException() {
    super(HttpStatus.CONFLICT, "Verification code not yet verified");
  }
}
