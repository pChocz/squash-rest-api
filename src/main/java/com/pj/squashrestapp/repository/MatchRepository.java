package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface MatchRepository
        extends JpaRepository<Match, Long>, SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

    @Query(
            """
          SELECT l.uuid FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
              WHERE m.uuid = :matchUuid
          """)
    UUID retrieveLeagueUuidOfMatch(@Param("matchUuid") UUID matchUuid);

    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "roundGroup.round.season.league"})
    Optional<Match> findMatchByUuid(UUID uuid);

    @Query(
            """
          SELECT m FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE (p1.uuid IN :playersUuids
                AND p2.uuid IN :playersUuids)
          """)
    @EntityGraph(
            attributePaths = {
                "firstPlayer",
                "secondPlayer",
                "setResults",
                "roundGroup.round.season.league",
            })
    List<Match> fetchHeadToHead(@Param("playersUuids") UUID[] playersUuids);

    @Query(
            """
          SELECT m FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonUuid) is null or s.uuid = :seasonUuid)
                AND (COALESCE(null, :groupNumber) is null or rg.number = :groupNumber)
                AND (COALESCE(null, :dateFrom) is null or r.date >= :dateFrom)
                AND (COALESCE(null, :dateTo) is null or r.date <= :dateTo)
                AND p1.uuid IN :playersUuids
                AND p2.uuid IN :playersUuids
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "roundGroup.round.season"})
    List<Match> fetchForSeveralPlayersForLeagueForSeasonForGroupNumber(
            @Param("leagueUuid") UUID leagueUuid,
            @Param("playersUuids") UUID[] playersUuids,
            @Param("seasonUuid") UUID seasonUuid,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("groupNumber") Integer groupNumber);

    @Query(
            """
          SELECT m FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (p1.uuid = :playerUuid OR p2.uuid = :playerUuid)
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "roundGroup.round.season"})
    List<Match> fetchByOnePlayerAgainstOthersAndLeagueId(
            @Param("leagueUuid") UUID leagueUuid, @Param("playerUuid") UUID playerUuid);

    @Query(
            """
          SELECT m FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
                WHERE (p1.uuid = :playerUuid OR p2.uuid = :playerUuid)
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "roundGroup.round.season.league"})
    List<Match> fetchByOnePlayerAgainstAllForAllLeagues(@Param("playerUuid") UUID playerUuid);

    @Query(
            """
          SELECT m FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonUuid) is null or s.uuid = :seasonUuid)
                AND (COALESCE(null, :groupNumber) is null or rg.number = :groupNumber)
                AND (COALESCE(null, :dateFrom) is null or r.date >= :dateFrom)
                AND (COALESCE(null, :dateTo) is null or r.date <= :dateTo)
                AND (p1.uuid = :playerUuid OR p2.uuid = :playerUuid)
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "roundGroup.round.season"})
    List<Match> fetchForOnePlayerForLeagueForSeasonForGroupNumber(
            @Param("leagueUuid") UUID leagueUuid,
            @Param("playerUuid") UUID playerUuid,
            @Param("seasonUuid") UUID seasonUuid,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("groupNumber") Integer groupNumber);

    @Query(
            """
          SELECT m.id FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonUuid) is null or s.uuid = :seasonUuid)
                AND (COALESCE(null, :groupNumber) is null or rg.number = :groupNumber)
                AND (COALESCE(null, :dateFrom) is null or r.date >= :dateFrom)
                AND (COALESCE(null, :dateTo) is null or r.date <= :dateTo)
                AND p1.uuid IN :playersUuids
                AND p2.uuid IN :playersUuids
          """)
    Page<Long> findIdsMultiple(
            Pageable pageable,
            @Param("leagueUuid") UUID leagueUuid,
            @Param("playersUuids") UUID[] playersUuids,
            @Param("seasonUuid") UUID seasonUuid,
            @Param("groupNumber") Integer groupNumber,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo);

    @Query(
            """
          SELECT m.id FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonUuid) is null or s.uuid = :seasonUuid)
                AND (COALESCE(null, :groupNumber) is null or rg.number = :groupNumber)
                AND (COALESCE(null, :dateFrom) is null or r.date >= :dateFrom)
                AND (COALESCE(null, :dateTo) is null or r.date <= :dateTo)
                AND (p1.uuid = :playerUuid or p2.uuid = :playerUuid)
          """)
    Page<Long> findIdsSingle(
            Pageable pageable,
            @Param("leagueUuid") UUID leagueUuid,
            @Param("playerUuid") UUID playerUuid,
            @Param("seasonUuid") UUID seasonUuid,
            @Param("groupNumber") Integer groupNumber,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo);

    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "roundGroup.round.season.league"})
    List<Match> findByIdIn(List<Long> matchIds);

    @Override
    @Modifying
    @Query("DELETE FROM Match m WHERE m.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Override
    @Query(
            """
          SELECT m.id FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);

    @Override
    @Query(
            """
          SELECT m.id FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
              WHERE s.uuid = :seasonUuid
          """)
    List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);
}
