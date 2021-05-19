package com.pj.squashrestapp.dto.scoreboard.headtohead;

import lombok.Getter;

import java.time.LocalDate;

/**
 *
 */
@Getter
public class HeadToHeadChartRow {

  private final LocalDate date;
  private final int numberOfSets;
  private final boolean firstPlayerWon;

  public HeadToHeadChartRow(final LocalDate date, final int numberOfSets, final boolean firstPlayerWon) {
    this.date = date;
    this.numberOfSets = numberOfSets;
    this.firstPlayerWon = firstPlayerWon;
  }

}
