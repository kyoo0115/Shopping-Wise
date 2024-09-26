package com.project.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class SignInResponseDto {

  private String token;
  private String email;

  public static SignInResponseDto of(String token, String email) {
    return SignInResponseDto.builder()
        .token(token)
        .email(email)
        .build();
  }
}