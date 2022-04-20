package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.EmailChangeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** */
public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Long> {

    EmailChangeToken findByToken(UUID token);

    List<EmailChangeToken> findAllByExpirationDateTimeBefore(LocalDateTime time);
}
