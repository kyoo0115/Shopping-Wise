package com.project.user.service;

import com.project.user.dto.request.SendVerificationAndCheckDuplicateRequestDto;
import com.project.user.dto.request.SignInRequestDto;
import com.project.user.dto.request.SignUpRequestDto;
import com.project.user.dto.request.VerifyCodeRequestDto;
import com.project.user.dto.response.SignInResponseDto;
import com.project.user.dto.response.SignUpResponseDto;
import com.project.user.entity.UserEntity;
import com.project.user.entity.VerificationEntity;
import com.project.user.exception.PasswordMismatchException;
import com.project.user.exception.UserEmailDuplicateException;
import com.project.user.exception.VerificationCodeExpiredException;
import com.project.user.exception.VerificationCodeNotFoundException;
import com.project.user.exception.VerificationCodeNotMatchedException;
import com.project.user.exception.VerificationCodeNotYetVerifiedException;
import com.project.user.provider.EmailProvider;
import com.project.user.provider.TokenProvider;
import com.project.user.repository.UserRepository;
import com.project.user.repository.VerificationRepository;
import com.project.user.util.VerificationCodeGeneratorUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final VerificationRepository verificationRepository;

  private final EmailProvider emailProvider;
  private final TokenProvider tokenProvider;

  private final PasswordEncoder passwordEncoder;

  public void checkDuplicateEmailAndSendVerification(
      SendVerificationAndCheckDuplicateRequestDto dto) {

    validateEmailNotExists(dto.getEmail());
    sendVerificationCode(dto.getEmail());
  }

  public void checkVerificationCode(VerifyCodeRequestDto dto) {
    VerificationEntity verificationEntity = getVerificationEntity(dto.getEmail());
    validateVerificationCode(verificationEntity, dto.getVerificationCode());
  }

  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto dto) {
    VerificationEntity verificationEntity = getVerificationEntity(dto.getEmail());
    validateEmailNotExists(dto.getEmail());
    validatePasswordsMatchForSignUp(dto.getPassword(), dto.getConfirmPassword());
    validateIfVerified(verificationEntity);

    String encodedPassword = passwordEncoder.encode(dto.getPassword());

    UserEntity userEntity = UserEntity.of(dto.getEmail(), encodedPassword);
    userRepository.save(userEntity);

    verificationRepository.deleteByEmail(dto.getEmail());

    return SignUpResponseDto.of(dto.getEmail());
  }

  public SignInResponseDto signIn(SignInRequestDto dto) {
    UserEntity userEntity = userRepository.findByEmail(dto.getEmail()).orElseThrow(
        () -> new UsernameNotFoundException("일치하는 이메일이 없습니다.")
    );

    validatePasswordsMatchForLogin(dto.getPassword(), userEntity.getPassword());

    LocalDateTime expiryDateTime = LocalDateTime.now().plusHours(1);
    String token = tokenProvider.createToken(userEntity.getEmail(), expiryDateTime);

    return SignInResponseDto.of(token, userEntity.getEmail());
  }

  public void signOut(HttpServletRequest request) {
    String token = tokenProvider.resolveToken(request);

    if (token != null) {
      if (tokenProvider.validateTokenStructure(token)) {
        tokenProvider.blacklistToken(token);
        log.info("Token has been successfully blacklisted.");
      }
    }
  }

  private void validateEmailNotExists(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new UserEmailDuplicateException();
    }
  }

  private void sendVerificationCode(String email) {
    String verificationCode = VerificationCodeGeneratorUtil.generateVerificationCode();
    String expirationTime = VerificationCodeGeneratorUtil.generateExpirationTime();

    emailProvider.sendVerificationEmail(email, verificationCode, expirationTime);
    verificationRepository.save(VerificationEntity.of(email, verificationCode));
  }

  private void validateVerificationCode(VerificationEntity verificationEntity,
      String verificationCode) {
    if (!verificationEntity.getVerificationCode().equals(verificationCode)) {
      throw new VerificationCodeNotMatchedException();
    }

    if (!verificationEntity.isVerified() &&
        verificationEntity.getExpiredAt() != null &&
        verificationEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new VerificationCodeExpiredException();
    }

    verificationEntity.setExpiredAt(null);
    verificationEntity.setVerified(true);
    verificationRepository.save(verificationEntity);
  }

  private VerificationEntity getVerificationEntity(String email) {
    return verificationRepository.findByEmail(email)
        .orElseThrow(VerificationCodeNotFoundException::new);
  }

  private void validateIfVerified(VerificationEntity verificationEntity) {
    if (!verificationEntity.isVerified()) {
      throw new VerificationCodeNotYetVerifiedException();
    }
  }

  private void validatePasswordsMatchForSignUp(String password, String confirmPassword) {
    if (!password.equals(confirmPassword)) {
      throw new PasswordMismatchException();
    }
  }

  public void validatePasswordsMatchForLogin(String rawPassword, String encodedPassword) {
    if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
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
