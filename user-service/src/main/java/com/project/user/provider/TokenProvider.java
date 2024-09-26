package com.project.user.provider;

import com.project.user.entity.BlacklistedTokenEntity;
import com.project.user.repository.BlacklistRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

  @Value("${spring.jwt.secret}")
  private String secretKey;

  private Key key;

  private final BlacklistRepository blacklistRepository;

  private static final String TOKEN_PREFIX = "Bearer ";
  private static final String HEADER_STRING = "Authorization";

  @PostConstruct
  public void init() {
    key = Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  /**
   * JWT 토큰 생성
   */
  public String createToken(String userId, LocalDateTime expiryDateTime) {
    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(new Date())
        .setExpiration(convertToDate(expiryDateTime))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * JWT 토큰 검증
   */
  public boolean isValidToken(String token) {
    if (!validateTokenStructure(token)) {
      return false;
    }

    if (isTokenBlacklisted(token)) {
      return false;
    }

    return !isTokenExpired(token);
  }

  /**
   * JWT 토큰에서 사용자 이름(이메일)을 추출합니다.
   */
  public String getUsernameFromToken(String token) {
    return parseClaims(token).getSubject();
  }

  /**
   * JWT 토큰에서 만료 날짜를 추출합니다.
   */
  public LocalDateTime getExpiryDateFromToken(String token) {
    return convertToLocalDateTime(parseClaims(token).getExpiration());
  }

  /**
   * JWT 토큰 블랙리스트 여부 확인
   */
  public boolean isTokenBlacklisted(String token) {
    return blacklistRepository.findByToken(token).isPresent();
  }

  /**
   * JWT 토큰 만료 여부 확인
   */
  public boolean isTokenExpired(String token) {
    return getExpiryDateFromToken(token).isBefore(LocalDateTime.now());
  }

  /**
   * JWT 토큰을 블랙리스트에 추가합니다.
   */
  public void blacklistToken(String token) {
    BlacklistedTokenEntity blacklistedTokenEntity = new BlacklistedTokenEntity();
    blacklistedTokenEntity.setToken(token);
    blacklistedTokenEntity.setExpiryDate(getExpiryDateFromToken(token));

    blacklistRepository.save(blacklistedTokenEntity);
  }

  /**
   * 요청에서 JWT 토큰 추출
   */
  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(HEADER_STRING);
    return (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) ? bearerToken.substring(7)
        : null;
  }

  public boolean validateTokenStructure(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  private Date convertToDate(LocalDateTime dateTime) {
    return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  private LocalDateTime convertToLocalDateTime(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}