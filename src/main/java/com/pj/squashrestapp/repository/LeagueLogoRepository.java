package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
            JOIN League l ON l.leagueLogo = ll
              WHERE l.uuid = :leagueUuid
              """)
  Optional<byte[]> extractLogoBlobByLeagueUuid(UUID leagueUuid);


  @Query("""
          SELECT ll.picture FROM LeagueLogo ll
            JOIN League l ON l.leagueLogo = ll
            JOIN Season s ON s.league = l
              WHERE s.uuid = :seasonUuid
              """)
  Optional<byte[]> extractLogoBlobBySeasonUuid(UUID seasonUuid);


  @Query("""
          SELECT ll.picture FROM LeagueLogo ll
            JOIN League l ON l.leagueLogo = ll
            JOIN Season s ON s.league = l
            JOIN Round r ON r.season = s
              WHERE r.uuid = :roundUuid
              """)
  Optional<byte[]> extractLogoBlobByRoundUuid(UUID roundUuid);

}
