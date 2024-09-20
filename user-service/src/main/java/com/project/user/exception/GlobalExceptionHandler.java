package com.project.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EmailSendErrorException.class)
  public ResponseEntity<?> handleEmailSendErrorException(EmailSendErrorException ex,
      WebRequest request) {
    return new ResponseEntity<>(new ErrorResponse("EMAIL_SEND_ERROR", ex.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
    return new ResponseEntity<>(
        new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static class ErrorResponse {

    private String errorCode;
    private String errorMessage;

    public ErrorResponse(String errorCode, String errorMessage) {
      this.errorCode = errorCode;
      this.errorMessage = errorMessage;
    }
  }
}