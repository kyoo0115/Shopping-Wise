package com.project.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

  @NotBlank(message = "email cannot be blank")
  @Email(message = "email is not valid")
  private String email;

  @NotBlank(message = "password cannot be blank")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$", message = "password is not valid")
  private String password;

  @NotBlank(message = "confirmPassword cannot be blank")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$", message = "confirmPassword is not valid")
  private String confirmPassword;

}
