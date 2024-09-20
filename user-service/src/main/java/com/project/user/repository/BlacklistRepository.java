package com.project.user.repository;

import com.project.user.entity.BlacklistedTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistedTokenEntity, String> {

  Optional<BlacklistedTokenEntity> findByToken(String token);
}
