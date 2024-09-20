package com.project.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "blacklisted_tokens")
public class BlacklistedTokenEntity {

  @Id
  private String token;

  private LocalDateTime expiryDate;
}
