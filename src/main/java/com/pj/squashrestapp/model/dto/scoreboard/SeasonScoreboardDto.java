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
    return (finishedRounds <= 4)
            ? finishedRounds
            : (finishedRounds <= 8)
            ? finishedRounds - 1
            : finishedRounds - 2;
  }

  public void sortRows() {
    Collections.sort(seasonScoreboardRows);
  }

}
