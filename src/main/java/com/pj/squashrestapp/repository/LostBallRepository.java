package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.LostBall;
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
public interface LostBallRepository
        extends JpaRepository<LostBall, Long>, SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

    @EntityGraph(attributePaths = {"season.league"})
    Optional<LostBall> findByUuid(UUID uuid);

    @Query(
            """
          SELECT lb FROM LostBall lb
            JOIN lb.season s
              WHERE s.uuid = :seasonUuid
          ORDER BY lb.date DESC, lb.id DESC
          """)
    @EntityGraph(
            attributePaths = {
                "player",
                "season.league",
            })
    List<LostBall> findBySeasonUuid(@Param("seasonUuid") UUID seasonUuid);

    @Query(
            """
          SELECT lb FROM LostBall lb
            JOIN lb.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    @EntityGraph(
            attributePaths = {
                "player",
                "season.id",
            })
    List<LostBall> findByLeagueUuid(@Param("leagueUuid") UUID leagueUuid);

    @Query(
            """
          SELECT l.uuid FROM LostBall lb
            JOIN lb.season s
            JOIN s.league l
              WHERE lb.uuid = :uuid
              """)
    UUID retrieveLeagueUuidOfLostBall(@Param("uuid") UUID uuid);

    @Override
    @Query(
            """
          SELECT lb.id FROM LostBall lb
            JOIN lb.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);

    @Override
    @Modifying
    @Query("DELETE FROM LostBall lb WHERE lb.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Override
    @Query(
            """
          SELECT lb.id FROM LostBall lb
            JOIN lb.season s
              WHERE s.uuid = :seasonUuid
          """)
    List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);
}
