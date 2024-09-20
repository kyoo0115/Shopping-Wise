package com.project.user.security;

import com.project.user.provider.TokenProvider;
import com.project.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;
  private final UserService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    String token = tokenProvider.resolveToken(request);

    if (token != null && tokenProvider.validateToken(token)) {
      authenticateUser(token, request);
    } else {
      log.warn("JWT Token is missing or invalid");
    }

    chain.doFilter(request, response);
  }

  private void authenticateUser(String token, HttpServletRequest request) {
    String username = tokenProvider.getUsernameFromToken(token);
    UserDetails userDetails = userService.loadUserByUsername(username);

    if (userDetails != null) {
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.info("사용자: {} Role: {}", username, authentication.getAuthorities().toString());
    }
  }
}