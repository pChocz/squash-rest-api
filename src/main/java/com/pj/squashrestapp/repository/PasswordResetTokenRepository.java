package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  PasswordResetToken findByToken(String token);

}
