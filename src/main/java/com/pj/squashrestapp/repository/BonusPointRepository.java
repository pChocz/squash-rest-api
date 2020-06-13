package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Blob;
import java.util.Collection;
import java.util.List;

public interface BonusPointRepository extends JpaRepository<BonusPoint, Long> {




  @EntityGraph(attributePaths = {
          "player",
  })
  List<BonusPoint> findByPlayerIdAndSeasonId(Long playerId, Long seasonId);


  @EntityGraph(attributePaths = {
          "player",
  })
  List<BonusPoint> findByPlayerIdIn(Collection<Long> playerIds);

}
