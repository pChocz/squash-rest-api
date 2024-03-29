package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Methods in this class implement 2 quite cool performance solutions:
 *
 * - Vlad Mihalcea's solution for multi-level-fetching:
 *   https://vladmihalcea.com/hibernate-facts-multi-level-fetching/
 *
 * - Durgaprasad Guduguntla's entity graph solution for ad-hoc
 *   setting attribute paths for entity graphs:
 *   https://medium.com/@gdprao/fixing-hibernate-n-1-problem-in-spring-boot-application-a99c38c5177d
 *
 * First solution makes it possible to perform multi-level fetching of:
 * - League
 *   - Season
 *     - Round
 *       - RoundGroup
 *         - Match
 *           - SetResult
 *
 * Second solution prevents additional queries to fill players fields.
 *
 * As a result we can extract entire league with a single query and
 * reconstruct it later with {@link EntityGraphBuildUtil} utility class.
 */
public interface SetResultRepository
        extends JpaRepository<SetResult, Long>, SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

    @Query(
            """
          SELECT sr FROM SetResult sr
            JOIN FETCH sr.match m
            JOIN FETCH m.roundGroup rg
            JOIN FETCH rg.round r
            JOIN FETCH r.season s
            JOIN FETCH s.league l
              WHERE l.uuid = :leagueUuid
          """)
    @EntityGraph(
            attributePaths = {
                "match.firstPlayer",
                "match.secondPlayer",
                "match.scores"
            })
    List<SetResult> fetchByLeagueUuid(@Param("leagueUuid") UUID leagueUuid);

    @Query(
            """
          SELECT sr FROM SetResult sr
            JOIN FETCH sr.match m
            JOIN FETCH m.roundGroup rg
            JOIN FETCH rg.round r
            JOIN FETCH r.season s
              WHERE s.uuid = :seasonUuid
          """)
    @EntityGraph(
            attributePaths = {
                    "match.firstPlayer",
                    "match.secondPlayer",
                    "match.roundGroup.round.season.league",
                    "match.scores"
            })
    List<SetResult> fetchBySeasonUuid(@Param("seasonUuid") UUID seasonUuid);

    @Query(
            """
          SELECT sr FROM SetResult sr
            JOIN FETCH sr.match m
            JOIN FETCH m.roundGroup rg
            JOIN FETCH rg.round r
              WHERE r.uuid = :roundUuid
          """)
    @EntityGraph(
            attributePaths = {
                "match.firstPlayer",
                "match.secondPlayer",
                "match.roundGroup.round.season",
                "match.roundGroup.round.season.league",
                    "match.scores",
            })
    List<SetResult> fetchByRoundUuid(@Param("roundUuid") UUID roundUuid);

    @Query(
            """
          SELECT sr FROM SetResult sr
            JOIN FETCH sr.match m
            JOIN FETCH m.roundGroup rg
              WHERE rg.id IN :ids
          """)
    @EntityGraph(
            attributePaths = {
                "match.firstPlayer",
                "match.secondPlayer",
                "match.roundGroup.round.season",
                "match.roundGroup.round.season.league",
                    "match.scores",
            })
    List<SetResult> fetchByRoundGroupsIds(@Param("ids") List<Long> ids);

    @Override
    @Modifying
    @Query("DELETE FROM SetResult sr WHERE sr.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Override
    @Query(
            """
          SELECT sr.id FROM SetResult sr
            JOIN sr.match m
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
          SELECT sr.id FROM SetResult sr
            JOIN sr.match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
              WHERE s.uuid = :seasonUuid
          """)
    List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);
}
