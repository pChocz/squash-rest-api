package com.pj.squashrestapp.model.dto.scoreboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unused")
@Getter
@Setter
public class SeasonScoreboardDto {

  final int allRounds = 10;

  Long seasonId;
  int seasonNumber;

  @JsonFormat(pattern = "yyyy-MM-dd")
  Date seasonStartDate;

  int finishedRounds;

  List<SeasonScoreboardRowDto> seasonScoreboardRows;

  public SeasonScoreboardDto(final Season season) {
    this.seasonId = season.getId();
    this.seasonNumber = season.getNumber();
    this.seasonStartDate = season.getStartDate();
    this.seasonScoreboardRows = new ArrayList<>();
    this.finishedRounds = (int) season
            .getRounds()
            .stream()
            .filter(Round::isFinished)
            .count();
  }

  public void sortRows() {
    Collections.sort(seasonScoreboardRows);
  }

}
