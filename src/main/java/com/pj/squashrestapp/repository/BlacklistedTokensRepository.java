package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BlacklistedTokensRepository extends JpaRepository<BlacklistedToken, Long> {

  List<BlacklistedToken> findAllByExpirationDateTimeBefore(LocalDateTime time);

  BlacklistedToken findByToken(String token);

}
