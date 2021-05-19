package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.aspects.LoggableQuery;
import com.pj.squashrestapp.dto.RoundDto;
import com.pj.squashrestapp.dto.SeasonDto;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class SeasonScoreboardDto implements LoggableQuery {

  private final SeasonDto season;
  private final int allRounds = 10;
  private final int finishedRounds;
  private final int countedRounds;
  private final List<SeasonScoreboardRowDto> seasonScoreboardRows;
  private final List<RoundDto> rounds;

  private final String xpPointsType;

  private final UUID previousSeasonUuid;
  private final UUID nextSeasonUuid;

  public SeasonScoreboardDto(final Season season) {
    this.season = new SeasonDto(season);
    this.seasonScoreboardRows = new ArrayList<>();

    this.previousSeasonUuid = null;
    this.nextSeasonUuid = null;

    this.xpPointsType = season.getXpPointsType();

    this.finishedRounds = (int) season
            .getRounds()
            .stream()
            .filter(Round::isFinished)
            .count();

    this.countedRounds = getNumberOfRoundsThatCount();

    this.rounds = season
            .getRounds()
            .stream()
            .map(RoundDto::new)
            .collect(Collectors.toList());
  }

  private int getNumberOfRoundsThatCount() {
    return (finishedRounds <= 4)
            ? finishedRounds
            : (finishedRounds <= 8)
            ? finishedRounds - 1
            : finishedRounds - 2;
  }

  public SeasonScoreboardDto(final Season season, final UUID previousSeasonUuid, final UUID nextSeasonUuid) {
    this.season = new SeasonDto(season);
    this.seasonScoreboardRows = new ArrayList<>();

    this.previousSeasonUuid = previousSeasonUuid;
    this.nextSeasonUuid = nextSeasonUuid;

    this.xpPointsType = season.getXpPointsType();

    this.finishedRounds = (int) season
            .getRounds()
            .stream()
            .filter(Round::isFinished)
            .count();

    this.countedRounds = getNumberOfRoundsThatCount();

    this.rounds = season
            .getRounds()
            .stream()
            .map(RoundDto::new)
            .collect(Collectors.toList());
  }

  public void sortByCountedPoints() {
    Collections.sort(seasonScoreboardRows);
  }

  public void sortByTotalPoints() {
    seasonScoreboardRows.sort(Comparator
            .comparing(SeasonScoreboardRowDto::getTotalPoints)
            .reversed());
  }

  public void sortByPretendersPoints() {
    seasonScoreboardRows.sort(Comparator
            .comparing(SeasonScoreboardRowDto::getCountedPointsPretenders)
            .reversed());
  }

  public boolean previousSeasonExists() {
    return this.previousSeasonUuid != null;
  }

  public boolean nextSeasonExists() {
    return this.nextSeasonUuid != null;
  }

  @Override
  public String message() {
    return toString();
  }

  @Override
  public String toString() {
    return "S: " + this.getSeason().getSeasonNumber()
           + "\t| " + this.getSeason().getLeagueName();
  }

}
