package com.pj.squashrestapp.dto;

import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.LostBall;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** */
@Getter
public final class LostBallsAggregatedForSeason {

  final UUID seasonUuid;
  final AtomicLongMap<UUID> pointsPerPlayerId;

  public LostBallsAggregatedForSeason(final UUID seasonUuid, final List<LostBall> lostBalls) {
    this.seasonUuid = seasonUuid;
    this.pointsPerPlayerId = AtomicLongMap.create();
    for (final LostBall lostBall : lostBalls) {
      final UUID playerId = lostBall.getPlayer().getUuid();
      final int count = lostBall.getCount();
      this.pointsPerPlayerId.getAndAdd(playerId, count);
    }
  }

  public int forPlayer(final UUID playerUuid) {
    return (int) this.pointsPerPlayerId.get(playerUuid);
  }
}
