package com.project.user.exception;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends GlobalException {

  public PasswordMismatchException() {
    super(HttpStatus.BAD_REQUEST, "Password mismatch");
  }
}
