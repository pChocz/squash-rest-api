package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.BonusPoint;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 */
public interface BonusPointRepository extends JpaRepository<BonusPoint, Long>, BulkDeletableByLeagueUuid {


  Optional<BonusPoint> findByUuid(UUID uuid);


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

  @Query("""
          SELECT bp.id FROM BonusPoint bp
            INNER JOIN bp.season s
            INNER JOIN s.league l
              WHERE l.uuid = :leagueUuid
              """)
  List<Long> fetchIdsByLeagueUuidRaw(UUID leagueUuid);

  @Modifying
  @Query("DELETE FROM BonusPoint bp WHERE bp.id IN ?1")
  void deleteAllByIdIn(List<Long> ids);

}
