package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.BlacklistedToken;
import com.pj.squashrestapp.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

  VerificationToken findByToken(String token);

  List<VerificationToken> findAllByExpirationDateTimeBefore(LocalDateTime time);

}
