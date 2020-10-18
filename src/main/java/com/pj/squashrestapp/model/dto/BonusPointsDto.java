package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.BonusPoint;
import lombok.Getter;

/**
 *
 */
@Getter
public final class BonusPointsDto {

  final PlayerDto player;
  final int points;

  public BonusPointsDto(final BonusPoint bonusPoint) {
    this.player = new PlayerDto(bonusPoint.getPlayer());
    this.points = bonusPoint.getPoints();
  }

}
