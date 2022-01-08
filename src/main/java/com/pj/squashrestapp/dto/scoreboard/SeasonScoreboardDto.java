package com.pj.squashrestapp.dto.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.pj.squashrestapp.aspects.LoggableQuery;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.RoundDto;
import com.pj.squashrestapp.dto.SeasonDto;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** */
@Getter
@NoArgsConstructor
public class SeasonScoreboardDto implements LoggableQuery {

  private SeasonDto season;
  private int allRounds;
  private int finishedRounds;
  private int countedRounds;
  private int countedRoundsOnSeasonFinished;
  private List<SeasonScoreboardRowDto> seasonScoreboardRows;
  private List<RoundDto> rounds;
  private String xpPointsType;
  private UUID previousSeasonUuid;
  private UUID nextSeasonUuid;
  private Map<UUID, SeasonStar> seasonStars;

  public SeasonScoreboardDto(final Season season) {
    this.season = new SeasonDto(season);
    this.seasonScoreboardRows = new ArrayList<>();
    this.previousSeasonUuid = null;
    this.nextSeasonUuid = null;
    this.xpPointsType = season.getXpPointsType();
    this.finishedRounds = (int) season.getRounds().stream().filter(Round::isFinished).count();
    this.allRounds = season.getNumberOfRounds();
    this.countedRoundsOnSeasonFinished =
        season.getNumberOfRounds() - season.getRoundsToBeDeducted();
    this.countedRounds = getNumberOfRoundsThatCount();
    this.rounds = season.getRounds().stream().map(RoundDto::new).collect(Collectors.toList());
    this.seasonStars = new HashMap<>();
  }

  public SeasonScoreboardDto(
      final Season season, final UUID previousSeasonUuid, final UUID nextSeasonUuid) {
    this.season = new SeasonDto(season);
    this.seasonScoreboardRows = new ArrayList<>();
    this.previousSeasonUuid = previousSeasonUuid;
    this.nextSeasonUuid = nextSeasonUuid;
    this.xpPointsType = season.getXpPointsType();
    this.finishedRounds = (int) season.getRounds().stream().filter(Round::isFinished).count();
    this.allRounds = season.getNumberOfRounds();
    this.countedRoundsOnSeasonFinished =
        season.getNumberOfRounds() - season.getRoundsToBeDeducted();
    this.countedRounds = getNumberOfRoundsThatCount();
    this.rounds = season.getRounds().stream().map(RoundDto::new).collect(Collectors.toList());
    this.seasonStars = new HashMap<>();
  }

  private int getNumberOfRoundsThatCount() {
    return (int) Math.ceil(finishedRounds * countedRoundsOnSeasonFinished / allRounds);
  }

  public void sortByCountedPoints() {
    if (finishedRounds == allRounds) {
      seasonScoreboardRows.sort(
          Comparator.comparingInt(SeasonScoreboardRowDto::getCountedPoints)
              .thenComparingInt(SeasonScoreboardRowDto::getTotalPoints)
              .thenComparingDouble(SeasonScoreboardRowDto::getAverageAsDouble)
              .reversed());
    } else {
      seasonScoreboardRows.sort(
          Comparator.comparingInt(SeasonScoreboardRowDto::getTotalPoints)
              .thenComparingInt(SeasonScoreboardRowDto::getCountedPoints)
              .thenComparingDouble(SeasonScoreboardRowDto::getAverageAsDouble)
              .reversed());
    }
  }

  public void sortByTotalPoints() {
    seasonScoreboardRows.sort(
        Comparator.comparing(SeasonScoreboardRowDto::getTotalPoints).reversed());
  }

  public void sortByPretendersPoints() {
    seasonScoreboardRows.sort(
        Comparator.comparing(SeasonScoreboardRowDto::getCountedPointsPretenders).reversed());
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
    return "Season Scoreboard - S: "
        + this.getSeason().getSeasonNumber()
        + " | "
        + this.getSeason().getLeagueName();
  }
}
