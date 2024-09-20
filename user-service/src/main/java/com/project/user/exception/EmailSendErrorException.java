package com.project.user.exception;

public class EmailSendErrorException extends RuntimeException {

  public EmailSendErrorException(String message) {
    super(message);
  }
}