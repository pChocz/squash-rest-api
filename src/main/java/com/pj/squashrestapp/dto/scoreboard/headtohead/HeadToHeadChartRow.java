package com.pj.squashrestapp.dto.scoreboard.headtohead;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

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
