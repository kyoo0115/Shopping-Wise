package com.project.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignUpResponseDto {

  private Long id;
  private String email;

  public static SignUpResponseDto of(String email) {
    return SignUpResponseDto.builder()
        .email(email)
        .build();
  }
}
