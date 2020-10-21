package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 *
 */
@Getter
public final class BonusPointsDto {

  final PlayerDto winner;
  final PlayerDto looser;
  final UUID uuid;
  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  final LocalDate date;
  final int points;

  public BonusPointsDto(final BonusPoint bonusPoint) {
    this.winner = new PlayerDto(bonusPoint.getWinner());
    this.looser = new PlayerDto(bonusPoint.getLooser());
    this.uuid = bonusPoint.getUuid();
    this.date = bonusPoint.getDate();
    this.points = bonusPoint.getPoints();
  }

}
