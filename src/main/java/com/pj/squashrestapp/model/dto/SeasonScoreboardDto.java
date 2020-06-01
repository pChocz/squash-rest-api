package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  public SeasonScoreboardDto(final Long seasonId, final int seasonNumber, final Date seasonStartDate) {
    this.seasonId = seasonId;
    this.seasonNumber = seasonNumber;
    this.seasonStartDate = seasonStartDate;
    this.seasonScoreboardRows = new ArrayList<>();
  }

  public void sortRows() {
    Collections.sort(seasonScoreboardRows);
  }

}
