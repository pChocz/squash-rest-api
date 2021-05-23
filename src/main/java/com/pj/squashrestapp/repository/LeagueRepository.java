package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 */
public interface LeagueRepository extends JpaRepository<League, Long> {


  @Override
  @EntityGraph(attributePaths = {
          "seasons",
          "additionalMatches",
  })
  List<League> findAll();


  @Query("SELECT l FROM League l")
  @EntityGraph(attributePaths = {
          "seasons",
  })
  List<League> findAllGeneralInfo();


  @Query("SELECT l FROM League l")
  List<League> findAllRaw();


  @EntityGraph(attributePaths = {
          "seasons",
          "trophiesForLeague",
          "rules",
          "additionalMatches",
  })
  League findByName(String name);


  Optional<League> findByUuid(UUID uuid);


  @Query("SELECT l FROM League l WHERE l.uuid = :leagueUuid")
  @EntityGraph(attributePaths = {
          "seasons.rounds.roundGroups.matches.setResults",
          "seasons.rounds.roundGroups.matches.firstPlayer",
          "seasons.rounds.roundGroups.matches.secondPlayer"
  })
  Optional<League> findByUuidForBackup(UUID leagueUuid);


  @Query("SELECT l.uuid FROM League l")
  List<UUID> findUuids();


  @Query("""
           SELECT COUNT(DISTINCT s), COUNT(DISTINCT r), COUNT(DISTINCT m), COUNT(DISTINCT sr), SUM(sr.firstPlayerScore) + SUM(sr.secondPlayerScore)
             FROM SetResult sr
             INNER JOIN sr.match m
             INNER JOIN m.roundGroup rg
             INNER JOIN rg.round r
             INNER JOIN r.season s
             INNER JOIN s.league l
               WHERE l.uuid = :uuid
               AND sr.firstPlayerScore IS NOT NULL
               AND sr.secondPlayerScore IS NOT NULL
          """)
  Object findAllCountsForLeagueByUuid(UUID uuid);


  @Query("""
           SELECT DISTINCT p1.id
             FROM SetResult sr
             INNER JOIN sr.match m
             INNER JOIN m.roundGroup rg
             INNER JOIN rg.round r
             INNER JOIN r.season s
             INNER JOIN s.league l
             INNER JOIN m.firstPlayer p1
             INNER JOIN m.secondPlayer p2
               WHERE l.uuid = :uuid
               AND sr.firstPlayerScore IS NOT NULL
               AND sr.secondPlayerScore IS NOT NULL
          """)
  List<Long> findPlayersIdsFirstPlayerForLeagueByUuid(UUID uuid);


  @Query("""
           SELECT DISTINCT p2.id
             FROM SetResult sr
             INNER JOIN sr.match m
             INNER JOIN m.roundGroup rg
             INNER JOIN rg.round r
             INNER JOIN r.season s
             INNER JOIN s.league l
             INNER JOIN m.firstPlayer p1
             INNER JOIN m.secondPlayer p2
               WHERE l.uuid = :uuid
               AND sr.firstPlayerScore IS NOT NULL
               AND sr.secondPlayerScore IS NOT NULL
          """)
  List<Long> findPlayersIdsSecondPlayerForLeagueByUuid(UUID uuid);


  @Query("""
           SELECT r.split, COUNT(DISTINCT r)
             FROM SetResult sr
             INNER JOIN sr.match m
             INNER JOIN m.roundGroup rg
             INNER JOIN rg.round r
             INNER JOIN r.season s
             INNER JOIN s.league l
             INNER JOIN m.firstPlayer p1
             INNER JOIN m.secondPlayer p2
               WHERE l.uuid = :uuid
               AND sr.firstPlayerScore IS NOT NULL
               AND sr.secondPlayerScore IS NOT NULL
             GROUP BY r.split
          """)
  List<Object> findRoundsPerSplitGroupedForLeagueByUuid(UUID uuid);


}
