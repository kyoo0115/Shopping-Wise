package com.project.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignInRequestDto {

  @NotNull(message = "email cannot be null")
  @Email(message = "email format is invalid.")
  private String email;

  @NotBlank(message = "confirmPassword cannot be blank")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$", message = "confirmPassword is not valid")
  private String password;
}
