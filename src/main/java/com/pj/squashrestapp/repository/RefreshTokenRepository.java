package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.RefreshToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  @Override
  @EntityGraph(attributePaths = {
          "player"
  })
  List<RefreshToken> findAll();


  RefreshToken findByToken(UUID token);


  List<RefreshToken> findAllByExpirationDateTimeBefore(LocalDateTime time);

}
