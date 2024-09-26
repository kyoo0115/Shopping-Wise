package com.project.user.exception;

import org.springframework.http.HttpStatus;

public class EmailSendErrorException extends GlobalException {

  public EmailSendErrorException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "Email send error");
  }
}