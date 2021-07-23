package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Match;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface MatchRepository extends JpaRepository<Match, Long>, BulkDeletableByLeagueUuid {

  @Query("""
          SELECT l.uuid FROM Match m
           JOIN m.roundGroup rg
           JOIN rg.round r
           JOIN r.season s
           JOIN s.league l
              WHERE m.uuid = :matchUuid
          """)
  UUID retrieveLeagueUuidOfMatch(UUID matchUuid);


  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season.league"
  })
  Optional<Match> findMatchByUuid(UUID uuid);


  @Query("""
          SELECT m FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
          INNER JOIN m.firstPlayer p1
          INNER JOIN m.secondPlayer p2
              WHERE (p1.uuid IN :playersUuids 
                 AND p2.uuid IN :playersUuids)
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season.league",
  })
  List<Match> fetchHeadToHead(@Param("playersUuids") UUID[] playersUuids);


  @Query("""
          SELECT m FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
          INNER JOIN m.firstPlayer p1
          INNER JOIN m.secondPlayer p2
            WHERE l.uuid = :leagueUuid
              AND (COALESCE(null, :seasonUuid) is null or s.uuid = :seasonUuid)
              AND (COALESCE(null, :groupNumber) is null or rg.number = :groupNumber)
              AND p1.uuid IN :playersUuids 
              AND p2.uuid IN :playersUuids
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> fetchForSeveralPlayersForLeagueForSeasonForGroupNumber(
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playersUuids") UUID[] playersUuids,
          @Param("seasonUuid") UUID seasonUuid,
          @Param("groupNumber") Integer groupNumber);


  @Query("""
          SELECT m FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
          INNER JOIN m.firstPlayer p1
          INNER JOIN m.secondPlayer p2
            WHERE l.uuid = :leagueUuid
              AND (p1.uuid = :playerUuid OR p2.uuid = :playerUuid)
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> fetchByOnePlayerAgainstOthersAndLeagueId(UUID leagueUuid, UUID playerUuid);


  @Query("""
          SELECT m FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
          INNER JOIN m.firstPlayer p1
          INNER JOIN m.secondPlayer p2
              WHERE (p1.uuid = :playerUuid OR p2.uuid = :playerUuid)
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season.league"
  })
  List<Match> fetchByOnePlayerAgainstAllForAllLeagues(UUID playerUuid);


  @Query("""
          SELECT m FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
          INNER JOIN m.firstPlayer p1
          INNER JOIN m.secondPlayer p2
            WHERE l.uuid = :leagueUuid
              AND (COALESCE(null, :seasonUuid) is null or s.uuid = :seasonUuid)
              AND (COALESCE(null, :groupNumber) is null or rg.number = :groupNumber)
              AND (p1.uuid = :playerUuid 
                OR p2.uuid = :playerUuid)
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> fetchForOnePlayerForLeagueForSeasonForGroupNumber(
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playerUuid") UUID playerUuid,
          @Param("seasonUuid") UUID seasonUuid,
          @Param("groupNumber") Integer groupNumber);


  @Query("""
          SELECT m.id FROM Match m
            INNER JOIN m.roundGroup rg
            INNER JOIN rg.round r
            INNER JOIN r.season s
            INNER JOIN s.league l
            INNER JOIN m.firstPlayer p1
            INNER JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonUuid) is null or s.uuid = :seasonUuid)
                AND (COALESCE(null, :groupNumber) is null or rg.number = :groupNumber)
                AND p1.uuid IN :playersUuids 
                AND p2.uuid IN :playersUuids
                """)
  Page<Long> findIdsMultiple(
          Pageable pageable,
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playersUuids") UUID[] playersUuids,
          @Param("seasonUuid") UUID seasonUuid,
          @Param("groupNumber") Integer groupNumber);


  @Query("""
          SELECT m.id FROM Match m
            INNER JOIN m.roundGroup rg
            INNER JOIN rg.round r
            INNER JOIN r.season s
            INNER JOIN s.league l
            INNER JOIN m.firstPlayer p1
            INNER JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonUuid) is null or s.uuid = :seasonUuid)
                AND (COALESCE(null, :groupNumber) is null or rg.number = :groupNumber)
                AND (p1.uuid = :playerUuid or p2.uuid = :playerUuid)
                """)
  Page<Long> findIdsSingle(
          Pageable pageable,
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playerUuid") UUID playerUuid,
          @Param("seasonUuid") UUID seasonUuid,
          @Param("groupNumber") Integer groupNumber);


  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> findByIdIn(List<Long> matchIds);

  @Modifying
  @Query("DELETE FROM Match m WHERE m.id IN ?1")
  void deleteAllByIdIn(List<Long> ids);

  @Query("""
          SELECT m.id FROM Match m
            INNER JOIN m.roundGroup rg
            INNER JOIN rg.round r
            INNER JOIN r.season s
            INNER JOIN s.league l
              WHERE l.uuid = :leagueUuid
              """)
  List<Long> fetchIdsByLeagueUuidRaw(UUID leagueUuid);
}
