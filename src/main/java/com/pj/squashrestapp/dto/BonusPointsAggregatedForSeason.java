package com.pj.squashrestapp.dto;

import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.BonusPoint;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

/** */
@Getter
public final class BonusPointsAggregatedForSeason {

  final UUID seasonUuid;
  final AtomicLongMap<UUID> pointsPerPlayerId;

  public BonusPointsAggregatedForSeason(final UUID seasonUuid, final List<BonusPoint> bonusPoints) {
    this.seasonUuid = seasonUuid;
    this.pointsPerPlayerId = AtomicLongMap.create();
    for (final BonusPoint bonusPoint : bonusPoints) {
      final UUID winnerId = bonusPoint.getWinner().getUuid();
      final UUID looserId = bonusPoint.getLooser().getUuid();
      final int points = bonusPoint.getPoints();
      this.pointsPerPlayerId.getAndAdd(winnerId, points);
      this.pointsPerPlayerId.getAndAdd(looserId, -points);
    }
  }

  public int forPlayer(final UUID playerUuid) {
    return (int) this.pointsPerPlayerId.get(playerUuid);
  }
}
