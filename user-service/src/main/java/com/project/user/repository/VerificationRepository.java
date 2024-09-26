package com.project.user.repository;

import com.project.user.entity.VerificationEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationEntity, Long> {

  Optional<VerificationEntity> findByEmail(String email);

  void deleteByEmail(String email);
}
