package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface LeagueLogoRepository extends JpaRepository<LeagueLogo, Long> {


  @Override
  @EntityGraph(attributePaths = {
          "league.uuid",
  })
  List<LeagueLogo> findAll();


  Optional<LeagueLogo> findByLeague(League league);


  @Query("""
          SELECT ll.picture FROM LeagueLogo ll
            JOIN ll.league l
              WHERE l.uuid = :leagueUuid
          """)
  Optional<byte[]> extractLogoBlobByLeagueUuid(@Param("leagueUuid") UUID leagueUuid);


  @Query("""
          SELECT ll.picture FROM LeagueLogo ll
            JOIN ll.league l
            JOIN l.seasons s
              WHERE s.uuid = :seasonUuid
          """)
  Optional<byte[]> extractLogoBlobBySeasonUuid(@Param("seasonUuid") UUID seasonUuid);


  @Query("""
          SELECT ll.picture FROM LeagueLogo ll
            JOIN ll.league l
            JOIN l.seasons s
            JOIN s.rounds r
              WHERE r.uuid = :roundUuid
          """)
  Optional<byte[]> extractLogoBlobByRoundUuid(@Param("roundUuid") UUID roundUuid);

}
