package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Season;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 *
 */
public interface SeasonRepository extends JpaRepository<Season, Long> {


  Optional<Season> findByUuid(UUID uuid);


  Optional<Season> findByLeagueAndNumber(League league, int number);


  @EntityGraph(attributePaths = {
          "rounds",
          "league"
  })
  Optional<Season> findSeasonByUuid(UUID uuid);


  @Query("""
          SELECT l.uuid FROM Season s
            JOIN League l ON s.league = l
              WHERE s.uuid = :seasonUuid
              """)
  UUID retrieveLeagueUuidOfSeason(UUID seasonUuid);


  @Query("""
          SELECT s.id FROM Season s 
            WHERE s.uuid = :seasonUuid
            """)
  Long findIdByUuid(UUID seasonUuid);


  @Query("""
          SELECT p1 from Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
          INNER JOIN m.firstPlayer p1
            WHERE s.uuid = :seasonUuid
            """)
  List<Player> extractSeasonPlayersFirst(UUID seasonUuid);


  @Query("""
          SELECT p2 from Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
          INNER JOIN m.secondPlayer p2
            WHERE s.uuid = :seasonUuid
            """)
  List<Player> extractSeasonPlayersSecond(UUID seasonUuid);


  @Query("""
          SELECT DISTINCT s FROM Season s
           INNER JOIN s.league l
              WHERE l.uuid = :leagueUuid
           ORDER BY s.startDate DESC
          """)
  List<Season> findCurrentSeasonForLeague(UUID leagueUuid, Pageable pageable);
}
