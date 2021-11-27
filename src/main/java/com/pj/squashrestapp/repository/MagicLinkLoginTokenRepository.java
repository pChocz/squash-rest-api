package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.MagicLoginLinkToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** */
public interface MagicLinkLoginTokenRepository extends JpaRepository<MagicLoginLinkToken, Long> {

  MagicLoginLinkToken findByToken(UUID token);

  List<MagicLoginLinkToken> findAllByExpirationDateTimeBefore(LocalDateTime time);
}
