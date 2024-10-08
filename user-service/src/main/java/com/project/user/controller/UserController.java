package com.project.user.controller;

import com.project.user.dto.request.SendVerificationAndCheckDuplicateRequestDto;
import com.project.user.dto.request.SignInRequestDto;
import com.project.user.dto.request.SignUpRequestDto;
import com.project.user.dto.request.VerifyCodeRequestDto;
import com.project.user.dto.response.SignInResponseDto;
import com.project.user.dto.response.SignUpResponseDto;
import com.project.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/check-duplicate-and-send-verification")
  public ResponseEntity<Void> checkDuplicateAndSendVerification(
      @RequestBody @Valid SendVerificationAndCheckDuplicateRequestDto dto
  ) {
    userService.checkDuplicateEmailAndSendVerification(dto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/check-verification-code")
  public ResponseEntity<Void> verifyCode(
      @RequestBody @Valid VerifyCodeRequestDto dto
  ) {
    userService.checkVerificationCode(dto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/sign-up")
  public ResponseEntity<SignUpResponseDto> signUp(
      @RequestBody @Valid SignUpRequestDto dto
  ) {
    return ResponseEntity.ok(userService.signUp(dto));
  }

  @PostMapping("/sign-in")
  public ResponseEntity<SignInResponseDto> signIn(
      @RequestBody @Valid SignInRequestDto dto
  ) {
    return ResponseEntity.ok(userService.signIn(dto));
  }

  @PostMapping("/sign-out")
  public ResponseEntity<Void> signOut(
      HttpServletRequest request
  ) {
    userService.signOut(request);
    return ResponseEntity.ok().build();
  }
}
