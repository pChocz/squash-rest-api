package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Round;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoundRepository
        extends JpaRepository<Round, Long>, SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

    Optional<Round> findByUuid(UUID uuid);

    @Query("SELECT r FROM Round r WHERE r.uuid = :uuid")
    @EntityGraph(attributePaths = {"season.league"})
    Round findByUuidWithSeasonAndLeague(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true)
    List<RoundLeagueUuidDto> findAllForPlayersEncounters(@Param("playerOneUuid") UUID playerOneUuid,
                                                         @Param("playerTwoUuid") UUID playerTwoUuid);

    @Query("SELECT r FROM Round r WHERE r.uuid = :uuid")
    @EntityGraph(attributePaths = {
            "season.league",
            "roundGroups.matches.firstPlayer",
            "roundGroups.matches.secondPlayer",
            "roundGroups.matches.setResults",
            "roundGroups.matches.scores"
    })
    Round findByUuidWithSeasonLeague(@Param("uuid") UUID uuid);

    @Query("SELECT r FROM Round r WHERE r.id IN :ids")
    @EntityGraph(attributePaths = {
            "season.league",
            "roundGroups.matches.firstPlayer",
            "roundGroups.matches.secondPlayer",
            "roundGroups.matches.setResults",
            "roundGroups.matches.scores"
    })
    List<Round> findByIdsWithSeasonLeague(@Param("ids") List<Long> ids);

    Optional<Round> findBySeasonLeagueUuidAndSeasonNumberAndNumber(UUID leagueUuid, int seasonNumber, int number);

    @Query(
            """
          SELECT l.uuid FROM Round r
            JOIN r.season s
            JOIN s.league l
              WHERE r.uuid = :roundUuid
          """)
    UUID retrieveLeagueUuidOfRound(@Param("roundUuid") UUID roundUuid);

    @Query(
            """
          SELECT r.uuid FROM Round r
            JOIN r.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    List<UUID> retrieveRoundsUuidsOfLeagueUuid(@Param("leagueUuid") UUID leagueUuid);

    @Query("SELECT r.id FROM Round r WHERE r.uuid = :roundUuid")
    Long findIdByUuid(@Param("roundUuid") UUID roundUuid);

    @Query(
            """
          SELECT DISTINCT r FROM Match m
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN FETCH r.season s
              WHERE (p1.uuid = :playerUuid OR p2.uuid = :playerUuid)
          ORDER BY r.date DESC
          """)
    List<Round> findMostRecentRoundOfPlayer(@Param("playerUuid") UUID playerUuid, Pageable pageable);

    @Query(
            """
          SELECT DISTINCT r FROM Match m
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          ORDER BY r.date DESC
          """)
    List<Round> findMostRecentRoundOfLeague(@Param("leagueUuid") UUID leagueUuid, Pageable pageable);

    @Override
    @Modifying
    @Query("DELETE FROM Round r WHERE r.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Override
    @Query(
            """
          SELECT r.id FROM Round r
            JOIN r.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);

    @Override
    @Query(
            """
          SELECT r.id FROM Round r
            JOIN r.season s
              WHERE s.uuid = :seasonUuid
          """)
    List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);

    @Query(
            """
          SELECT
            CASE WHEN COUNT(m) > 0
              THEN true
              ELSE false
            END
          FROM Match m
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
            JOIN m.roundGroup rg
            JOIN rg.round r
              WHERE (p1.uuid = :playerUuid OR p2.uuid = :playerUuid)
                AND r.uuid = :roundUuid
          """)
    boolean checkIfPlayerOfRound(@Param("roundUuid") UUID roundUuid, @Param("playerUuid") UUID playerUuid);
}
