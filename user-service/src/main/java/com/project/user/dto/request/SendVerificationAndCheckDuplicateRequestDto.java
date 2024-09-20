package com.project.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendVerificationAndCheckDuplicateRequestDto {

  @NotBlank(message = "email cannot be blank")
  @Email(message = "email is not valid")
  private String email;
}
