package com.pj.squashrestapp.service;

import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 *
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class BonusPointsAggregatedForSeason {

  @EqualsAndHashCode.Include
  final Long seasonId;
  final AtomicLongMap<Long> pointsPerPlayerId;

  public BonusPointsAggregatedForSeason(final Long seasonId, final List<BonusPoint> bonusPoints) {
    this.seasonId = seasonId;
    this.pointsPerPlayerId = AtomicLongMap.create();
    for (final BonusPoint bonusPoint : bonusPoints) {
      final Long playerId = bonusPoint.getPlayer().getId();
      final int points = bonusPoint.getPoints();
      this.pointsPerPlayerId.getAndAdd(playerId, points);
    }
  }

  public int forPlayer(final Long playerId) {
    return (int) this
            .pointsPerPlayerId
            .get(playerId);
  }

}
