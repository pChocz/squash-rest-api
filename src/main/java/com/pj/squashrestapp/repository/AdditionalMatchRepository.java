package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface AdditionalMatchRepository
        extends JpaRepository<AdditionalMatch, Long>, SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "league"})
    Optional<AdditionalMatch> findByUuid(UUID uuid);

    @Query(
            """
          SELECT m FROM AdditionalMatch m
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE (p1 = :player OR p2 = :player)
                AND m.league = :league
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "league"})
    List<AdditionalMatch> fetchForSinglePlayerForLeague(@Param("player") Player player, @Param("league") League league);

    @Query(
            """
          SELECT m FROM AdditionalMatch m
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE (p1 = :player OR p2 = :player)
          """)
    @EntityGraph(
            attributePaths = {
                "firstPlayer",
                "secondPlayer",
                "setResults",
            })
    List<AdditionalMatch> fetchAllForSinglePlayer(@Param("player") Player player);

    @Query(
            """
          SELECT m FROM AdditionalMatch m
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE (p1.uuid IN :playersUuids
                AND  p2.uuid IN :playersUuids)
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "league"})
    List<AdditionalMatch> fetchHeadToHead(@Param("playersUuids") UUID[] playersUuids);

    @Query(
            """
          SELECT m FROM AdditionalMatch m
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE (p1.uuid IN :playersUuids
                AND  p2.uuid IN :playersUuids)
                AND  m.league = :league
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "league"})
    List<AdditionalMatch> fetchForMultiplePlayersForLeague(
            @Param("playersUuids") UUID[] playersUuids, @Param("league") League league);

    @Query(
            """
          SELECT m FROM AdditionalMatch m
            JOIN m.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonNumber) is null or m.seasonNumber = :seasonNumber)
                AND p1.uuid IN :playersUuids
                AND p2.uuid IN :playersUuids
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults"})
    List<AdditionalMatch> fetchForSeveralPlayersForLeagueForSeasonNumber(
            @Param("leagueUuid") UUID leagueUuid,
            @Param("playersUuids") UUID[] playersUuids,
            @Param("seasonNumber") Integer seasonNumber);

    @Query(
            """
          SELECT m FROM AdditionalMatch m
            JOIN m.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonNumber) is null or m.seasonNumber = :seasonNumber)
                AND (p1.uuid = :playerUuid or p2.uuid = :playerUuid)
          """)
    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults"})
    List<AdditionalMatch> fetchForSinglePlayerForLeagueForSeasonNumber(
            @Param("leagueUuid") UUID leagueUuid,
            @Param("playerUuid") UUID playerUuid,
            @Param("seasonNumber") Integer seasonNumber);

    @Query(
            """
          SELECT m.id FROM AdditionalMatch m
            JOIN m.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonNumber) is null or m.seasonNumber = :seasonNumber)
                AND p1.uuid IN :playersUuids
                AND p2.uuid IN :playersUuids
          """)
    Page<Long> findIdsMultiple(
            Pageable pageable,
            @Param("leagueUuid") UUID leagueUuid,
            @Param("playersUuids") UUID[] playersUuids,
            @Param("seasonNumber") Integer seasonNumber);

    @Query(
            """
          SELECT m.id FROM AdditionalMatch m
            JOIN m.league l
            JOIN m.firstPlayer p1
            JOIN m.secondPlayer p2
              WHERE l.uuid = :leagueUuid
                AND (COALESCE(null, :seasonNumber) is null or m.seasonNumber = :seasonNumber)
                AND (p1.uuid = :playerUuid or p2.uuid = :playerUuid)
                """)
    Page<Long> findIdsSingle(
            Pageable pageable,
            @Param("leagueUuid") UUID leagueUuid,
            @Param("playerUuid") UUID playerUuid,
            @Param("seasonNumber") Integer seasonNumber);

    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "league"})
    List<AdditionalMatch> findByIdIn(List<Long> matchIds);

    @EntityGraph(attributePaths = {"firstPlayer", "secondPlayer", "setResults", "league"})
    List<AdditionalMatch> findAllByLeagueOrderByDateDescIdDesc(League league);

    @Override
    @Query(
            """
            SELECT m.id FROM AdditionalMatch m
              JOIN Season s ON m.seasonNumber = s.number AND m.league = s.league
                WHERE s.uuid = :seasonUuid
            """)
    List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);

    @Override
    @Modifying
    @Query("DELETE FROM AdditionalMatch m WHERE m.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Override
    @Query(
            """
            SELECT m.id FROM AdditionalMatch m
              JOIN m.league l
                WHERE l.uuid = :leagueUuid
            """)
    List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);
}
