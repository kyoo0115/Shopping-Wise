package com.project.user.provider;

import com.project.user.entity.BlacklistedTokenEntity;
import com.project.user.repository.BlacklistRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
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

  /**
   * 주어진 사용자 ID와 만료 날짜로 JWT 토큰을 생성합니다.
   */
  private String createToken(String userId, LocalDateTime expiryDateTime) {
    Date expiryDate = Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant());
    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * 주어진 JWT 토큰의 유효성을 검증합니다.
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * JWT 토큰에서 사용자 이름(이메일)을 추출합니다.
   */
  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  /**
   * JWT 토큰에서 만료 날짜를 추출합니다.
   */
  public LocalDateTime getExpiryDateFromToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    Date expiration = claims.getExpiration();
    return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  /**
   * 주어진 JWT 토큰이 만료되었는지 확인합니다.
   */
  public boolean isTokenExpired(String token) {
    LocalDateTime expiryDate = getExpiryDateFromToken(token);
    return expiryDate.isBefore(LocalDateTime.now());
  }

  /**
   * 주어진 JWT 토큰이 블랙리스트에 있는지 확인합니다.
   */
  public boolean isTokenBlacklisted(String token) {
    Optional<BlacklistedTokenEntity> blacklistedToken = blacklistRepository.findByToken(token);
    return blacklistedToken.isPresent();
  }

  /**
   * 주어진 JWT 토큰을 블랙리스트에 추가합니다.
   */
  public void blacklistToken(String token) {
    LocalDateTime expiryDate = getExpiryDateFromToken(token);
    BlacklistedTokenEntity blacklistedTokenEntity = new BlacklistedTokenEntity();
    blacklistedTokenEntity.setToken(token);
    blacklistedTokenEntity.setExpiryDate(expiryDate);
    blacklistRepository.save(blacklistedTokenEntity);
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(HEADER_STRING);
    if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }
}