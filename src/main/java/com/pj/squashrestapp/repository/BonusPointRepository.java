package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.BonusPoint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface BonusPointRepository extends JpaRepository<BonusPoint, Long> {

  Optional<BonusPoint> findByUuid(UUID uuid);

  @Query("""
          SELECT bp FROM BonusPoint bp
            JOIN Season s ON bp.season = s
            JOIN Player winner ON bp.winner = winner
            JOIN Player looser ON bp.looser = looser
              WHERE s.uuid = :seasonUuid
                AND (winner.uuid = :playerUuid OR looser.uuid = :playerUuid)
          """)
  @EntityGraph(attributePaths = {
          "winner",
          "looser",
  })
  List<BonusPoint> findByPlayerUuidAndSeasonUuid(UUID playerUuid, UUID seasonUuid);


  @Query("""
          SELECT bp FROM BonusPoint bp
            JOIN Season s ON bp.season = s
            WHERE s.uuid = :seasonUuid
            ORDER BY bp.date DESC, bp.id DESC
          """)
  @EntityGraph(attributePaths = {
          "winner",
          "looser",
          "season.league",
  })
  List<BonusPoint> findBySeasonUuid(UUID seasonUuid);


  @Query("""
          SELECT bp FROM BonusPoint bp
            JOIN Season s ON bp.season = s
            JOIN League l ON s.league = l
            WHERE l.uuid = :leagueUuid
          """)
  @EntityGraph(attributePaths = {
          "winner",
          "looser",
          "season.id",
  })
  List<BonusPoint> findByLeagueUuid(UUID leagueUuid);


  @Query("""
          SELECT l.uuid FROM BonusPoint bp
           JOIN Season s ON bp.season = s
           JOIN League l ON s.league = l
              WHERE bp.uuid = :uuid
          """)
  UUID retrieveLeagueUuidOfBonusPoint(UUID uuid);

}
