package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.EmailChangeToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** */
public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Long> {

  EmailChangeToken findByToken(UUID token);

  List<EmailChangeToken> findAllByExpirationDateTimeBefore(LocalDateTime time);
}
