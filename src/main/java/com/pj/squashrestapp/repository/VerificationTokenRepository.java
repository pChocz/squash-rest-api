package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.VerificationToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Override
    @EntityGraph(attributePaths = {"player"})
    List<VerificationToken> findAll();

    VerificationToken findByToken(UUID token);

    @EntityGraph(attributePaths = {"player.authorities"})
    List<VerificationToken> findAllByExpirationDateTimeBefore(LocalDateTime time);
}
