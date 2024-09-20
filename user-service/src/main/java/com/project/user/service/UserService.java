package com.project.user.service;

import com.project.user.dto.request.SendVerificationAndCheckDuplicateRequestDto;
import com.project.user.dto.request.SignInRequestDto;
import com.project.user.dto.request.SignUpRequestDto;
import com.project.user.dto.request.VerifyCodeRequestDto;
import com.project.user.dto.response.SignInResponseDto;
import com.project.user.dto.response.SignUpResponseDto;
import com.project.user.entity.UserEntity;
import com.project.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  public void checkDuplicateEmailAndSendVerification(
      SendVerificationAndCheckDuplicateRequestDto dto) {

    validateEmailNotExists(dto.getEmail());
    sendVerificationCode(dto.getEmail());
  }

  public void checkVerificationCode(VerifyCodeRequestDto dto) {

  }

  public SignUpResponseDto signUp(SignUpRequestDto dto, HttpServletRequest request,
      HttpServletResponse response) {
    return null;
  }

  public SignInResponseDto signIn(SignInRequestDto dto, HttpServletRequest request,
      HttpServletResponse response) {
    return null;
  }

  public void signOut(HttpServletRequest request, HttpServletResponse response) {

  }

  private void validateEmailNotExists(String email) {
    if(userRepository.findByEmail(email).isPresent()) {
      throw new UserEmailDuplicateException("User already exists with email: " + email);
    }
  }

  private void sendVerificationCode(String email) {
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

    if (optionalUser.isEmpty()) {
      throw new UsernameNotFoundException("User not found with username: " + email);
    }

    UserEntity userEntity = optionalUser.get();

    return User.builder()
        .username(userEntity.getEmail())
        .password(userEntity.getPassword())
        .authorities(userEntity.getRoles())
        .build();
  }
}
