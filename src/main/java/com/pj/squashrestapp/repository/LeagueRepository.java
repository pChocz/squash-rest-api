package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface LeagueRepository extends JpaRepository<League, Long> {

    @Override
    @EntityGraph(
            attributePaths = {
                    "seasons",
                    "additionalMatches"
            })
    List<League> findAll();

    @Query("SELECT l FROM League l")
    @EntityGraph(
            attributePaths = {
                "seasons",
            })
    List<League> findAllGeneralInfo();

    @Query("SELECT l FROM League l")
    List<League> findAllRaw();

    @EntityGraph(
            attributePaths = {
                "seasons",
                "trophiesForLeague",
                "rules",
                "additionalMatches",
            })
    League findByName(String name);

    Optional<League> findByUuid(UUID uuid);

    @Query("SELECT l FROM League l WHERE l.uuid = :leagueUuid")
    @EntityGraph(
            attributePaths = {
                "seasons.rounds.roundGroups.matches.setResults",
                "seasons.rounds.roundGroups.matches.firstPlayer",
                "seasons.rounds.roundGroups.matches.secondPlayer"
            })
    Optional<League> findByUuidForBackup(@Param("leagueUuid") UUID leagueUuid);

    @Query("SELECT l.uuid FROM League l")
    List<UUID> findUuids();

    @Query(
            """
           SELECT COUNT(DISTINCT s), COUNT(DISTINCT r), COUNT(DISTINCT m), COUNT(DISTINCT sr), SUM(sr.firstPlayerScore) + SUM(sr.secondPlayerScore) FROM SetResult sr
             JOIN sr.match m
             JOIN m.roundGroup rg
             JOIN rg.round r
             JOIN r.season s
             JOIN s.league l
               WHERE l.uuid = :uuid
                 AND sr.firstPlayerScore IS NOT NULL
                 AND sr.secondPlayerScore IS NOT NULL
          """)
    Object findAllCountsForLeagueByUuid(@Param("uuid") UUID uuid);

    @Query(
            """
           SELECT DISTINCT p1.id FROM SetResult sr
             JOIN sr.match m
             JOIN m.roundGroup rg
             JOIN rg.round r
             JOIN r.season s
             JOIN s.league l
             JOIN m.firstPlayer p1
             JOIN m.secondPlayer p2
               WHERE l.uuid = :uuid
                 AND sr.firstPlayerScore IS NOT NULL
                 AND sr.secondPlayerScore IS NOT NULL
          """)
    List<Long> findPlayersIdsFirstPlayerForLeagueByUuid(@Param("uuid") UUID uuid);

    @Query(
            """
           SELECT DISTINCT p2.id FROM SetResult sr
             JOIN sr.match m
             JOIN m.roundGroup rg
             JOIN rg.round r
             JOIN r.season s
             JOIN s.league l
             JOIN m.firstPlayer p1
             JOIN m.secondPlayer p2
               WHERE l.uuid = :uuid
                 AND sr.firstPlayerScore IS NOT NULL
                 AND sr.secondPlayerScore IS NOT NULL
          """)
    List<Long> findPlayersIdsSecondPlayerForLeagueByUuid(@Param("uuid") UUID uuid);

    @Query(
            """
           SELECT r.split, COUNT(DISTINCT r) FROM SetResult sr
             JOIN sr.match m
             JOIN m.roundGroup rg
             JOIN rg.round r
             JOIN r.season s
             JOIN s.league l
             JOIN m.firstPlayer p1
             JOIN m.secondPlayer p2
               WHERE l.uuid = :uuid
                 AND sr.firstPlayerScore IS NOT NULL
                 AND sr.secondPlayerScore IS NOT NULL
           GROUP BY r.split
          """)
    List<Object> findRoundsPerSplitGroupedForLeagueByUuid(@Param("uuid") UUID uuid);

    @Query("SELECT l FROM League l WHERE l.uuid = :uuid")
    League findByUuidRaw(@Param("uuid") UUID uuid);
}
