package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.LeagueLogo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Blob;
import java.util.List;

public interface LeagueLogoRepository extends JpaRepository<LeagueLogo, Long> {

  @Query("""
          SELECT ll.picture FROM LeagueLogo ll
            JOIN League l ON l.leagueLogo = ll
            WHERE l.id = :leagueId
          """)
  byte[] extractLogoBlob(@Param("leagueId") Long leagueId);

  @EntityGraph(attributePaths = {
          "league.id",
  })
  List<LeagueLogo> findAll();

}
