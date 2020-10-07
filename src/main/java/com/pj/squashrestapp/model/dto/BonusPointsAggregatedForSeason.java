package com.pj.squashrestapp.model.dto;

import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.BonusPoint;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Getter
public final class BonusPointsAggregatedForSeason {

  final UUID seasonUuid;
  final AtomicLongMap<UUID> pointsPerPlayerId;

  public BonusPointsAggregatedForSeason(final UUID seasonUuid, final List<BonusPoint> bonusPoints) {
    this.seasonUuid = seasonUuid;
    this.pointsPerPlayerId = AtomicLongMap.create();
    for (final BonusPoint bonusPoint : bonusPoints) {
      final UUID playerId = bonusPoint.getPlayer().getUuid();
      final int points = bonusPoint.getPoints();
      this.pointsPerPlayerId.getAndAdd(playerId, points);
    }
  }

  public int forPlayer(final UUID playerUuid) {
    return (int) this
            .pointsPerPlayerId
            .get(playerUuid);
  }

}
