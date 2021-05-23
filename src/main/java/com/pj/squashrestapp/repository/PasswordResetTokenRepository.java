package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.PasswordResetToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  PasswordResetToken findByToken(UUID token);

  List<PasswordResetToken> findAllByExpirationDateTimeBefore(LocalDateTime time);
}
