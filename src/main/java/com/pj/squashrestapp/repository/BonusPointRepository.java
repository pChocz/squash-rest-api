package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.BonusPoint;
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
public interface BonusPointRepository
        extends JpaRepository<BonusPoint, Long>, SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

    @EntityGraph(attributePaths = {"season.league"})
    Optional<BonusPoint> findByUuid(UUID uuid);

    @Query(
            """
          SELECT bp FROM BonusPoint bp
            JOIN bp.season s
              WHERE s.uuid = :seasonUuid
          ORDER BY bp.date DESC, bp.id DESC
          """)
    @EntityGraph(
            attributePaths = {
                "winner",
                "looser",
                "season.league",
            })
    List<BonusPoint> findBySeasonUuid(@Param("seasonUuid") UUID seasonUuid);

    @Query(
            """
          SELECT bp FROM BonusPoint bp
            JOIN bp.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    @EntityGraph(
            attributePaths = {
                "winner",
                "looser",
                "season.id",
            })
    List<BonusPoint> findByLeagueUuid(@Param("leagueUuid") UUID leagueUuid);

    @Query(
            """
          SELECT l.uuid FROM BonusPoint bp
            JOIN bp.season s
            JOIN s.league l
              WHERE bp.uuid = :uuid
              """)
    UUID retrieveLeagueUuidOfBonusPoint(@Param("uuid") UUID uuid);

    @Override
    @Query(
            """
          SELECT bp.id FROM BonusPoint bp
            JOIN bp.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);

    @Override
    @Modifying
    @Query("DELETE FROM BonusPoint bp WHERE bp.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Override
    @Query(
            """
          SELECT bp.id FROM BonusPoint bp
            JOIN bp.season s
              WHERE s.uuid = :seasonUuid
          """)
    List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);
}
