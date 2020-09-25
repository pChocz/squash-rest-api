package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.BonusPoint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BonusPointRepository extends JpaRepository<BonusPoint, Long> {

  @Query("""
          SELECT bp FROM BonusPoint bp
            JOIN Season s ON bp.season = s
            JOIN Player p ON bp.player = p
              WHERE s.uuid = :seasonUuid
                AND p.uuid = :playerUuid 
          """)
  @EntityGraph(attributePaths = {
          "player",
  })
  List<BonusPoint> findByPlayerUuidAndSeasonUuid(UUID playerUuid, UUID seasonUuid);


  @EntityGraph(attributePaths = {
          "player",
  })
  List<BonusPoint> findByPlayerIdIn(Collection<Long> playerIds);




  @Query("""
          SELECT bp FROM BonusPoint bp
            JOIN Season s ON bp.season = s
            WHERE s.id = :seasonId
          """)
  @EntityGraph(attributePaths = {
          "player",
          "season.id",
  })
  List<BonusPoint> findBySeasonId(@Param("seasonId") Long seasonId);


  @Query("""
          SELECT bp FROM BonusPoint bp
            JOIN Season s ON bp.season = s
            JOIN League l ON s.league = l
            WHERE l.id = :leagueId
          """)
  @EntityGraph(attributePaths = {
          "player",
          "season.id",
  })
  List<BonusPoint> findByLeagueId(@Param("leagueId") Long leagueId);


}
