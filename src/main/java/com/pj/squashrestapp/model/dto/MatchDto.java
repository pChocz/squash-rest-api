package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Getter
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MatchDto {

  @EqualsAndHashCode.Include
  Long matchId;
  PlayerDto firstPlayer;
  PlayerDto secondPlayer;

  Long roundGroupId;
  int roundGroupNumber;

  Long roundId;
  int roundNumber;
  @JsonFormat(pattern = "yyyy-MM-dd")
  Date roundDate;

  Long seasonId;
  int seasonNumber;

  List<SetDto> sets;

  public MatchDto(final Match match) {
    final RoundGroup roundGroup = match.getRoundGroup();
    final Round round = roundGroup.getRound();
    final Season season = round.getSeason();

    this.matchId = match.getId();
    this.firstPlayer = new PlayerDto(match.getFirstPlayer());
    this.secondPlayer = new PlayerDto(match.getSecondPlayer());
    this.roundGroupId = roundGroup.getId();
    this.roundGroupNumber = roundGroup.getNumber();
    this.roundDate = round.getDate();
    this.roundId = round.getId();
    this.roundNumber = round.getNumber();
    this.seasonId = season.getId();
    this.seasonNumber = season.getNumber();

    this.sets = new ArrayList<>();
    for (final SetResult setResult : match.getSetResults()) {
      this.sets.add(new SetDto(setResult));
    }
  }

  @Override
  public String toString() {
    return "[" + matchId + "] " + firstPlayer + " vs. " + secondPlayer + " : " + sets;
  }

}
