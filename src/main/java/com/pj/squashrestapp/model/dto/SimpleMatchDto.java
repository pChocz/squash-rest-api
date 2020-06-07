package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.SetResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Getter
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SimpleMatchDto {

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

  public SimpleMatchDto(final Match match) {
    this.matchId = match.getId();
    this.firstPlayer = new PlayerDto(match.getFirstPlayer());
    this.secondPlayer = new PlayerDto(match.getSecondPlayer());
    this.roundGroupId = match.getRoundGroup().getId();
    this.roundGroupNumber = match.getRoundGroup().getNumber();
    this.roundDate = match.getRoundGroup().getRound().getDate();
    this.roundId = match.getRoundGroup().getRound().getId();
    this.roundNumber = match.getRoundGroup().getRound().getNumber();
    this.seasonId = match.getRoundGroup().getRound().getSeason().getId();
    this.seasonNumber = match.getRoundGroup().getRound().getSeason().getNumber();

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
