package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  PasswordResetToken findByToken(String token);

  List<PasswordResetToken> findAllByExpirationDateTimeBefore(LocalDateTime time);

}
