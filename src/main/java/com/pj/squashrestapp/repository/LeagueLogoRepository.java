package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.LeagueLogo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 *
 */
public interface LeagueLogoRepository extends JpaRepository<LeagueLogo, Long> {


  @Override
  @EntityGraph(attributePaths = {
          "league.uuid",
  })
  List<LeagueLogo> findAll();


  @Query("""
          SELECT ll.picture FROM LeagueLogo ll
            JOIN League l ON l.leagueLogo = ll
              WHERE l.uuid = :leagueUuid
              """)
  byte[] extractLogoBlob(UUID leagueUuid);

}
