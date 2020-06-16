package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.LeagueLogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Blob;

public interface LeagueLogoRepository extends JpaRepository<LeagueLogo, Long> {

  @Query("""
          SELECT ll.picture FROM LeagueLogo ll
            JOIN League l ON l.leagueLogo = ll
            WHERE l.id = :leagueId
          """)
  Blob extractLogoBlob(@Param("leagueId") Long leagueId);

}
