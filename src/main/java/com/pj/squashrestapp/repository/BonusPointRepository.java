package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.BonusPoint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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
