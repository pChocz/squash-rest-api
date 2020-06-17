package com.pj.squashrestapp.model.dto.scoreboard;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.SeasonDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Getter
public class SeasonScoreboardDto {

  private final SeasonDto season;
  private final int allRounds = 10;
  private final int finishedRounds;
  private final int countedRounds;
  private final List<SeasonScoreboardRowDto> seasonScoreboardRows;

  public SeasonScoreboardDto(final Season season) {
    this.season = new SeasonDto(season);
    this.seasonScoreboardRows = new ArrayList<>();
    this.finishedRounds = (int) season
            .getRounds()
            .stream()
            .filter(Round::isFinished)
            .count();
    this.countedRounds = getNumberOfRoundsThatCount();
  }

  private int getNumberOfRoundsThatCount() {
    final int numberOfRoundsThatCount;
    if (finishedRounds <= 4) {
      numberOfRoundsThatCount = finishedRounds;
    } else if (finishedRounds <= 8) {
      numberOfRoundsThatCount = finishedRounds - 1;
    } else {
      numberOfRoundsThatCount = finishedRounds - 2;
    }
    return numberOfRoundsThatCount;
  }

  public void sortRows() {
    Collections.sort(seasonScoreboardRows);
  }

}
