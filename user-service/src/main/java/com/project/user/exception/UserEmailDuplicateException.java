package com.project.user.exception;

import org.springframework.http.HttpStatus;

public class UserEmailDuplicateException extends GlobalException {

  public UserEmailDuplicateException() {
    super(HttpStatus.BAD_REQUEST, "User has a duplicate email");
  }
}
