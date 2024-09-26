package com.project.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerifyCodeRequestDto {

  @NotBlank(message = "email cannot be blank.")
  @Email(message = "email format is invalid.")
  private String email;

  @NotBlank(message = "verificationCode cannot be blank.")
  private String verificationCode;
}
