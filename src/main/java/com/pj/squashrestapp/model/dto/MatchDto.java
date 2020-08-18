package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.entityhelper.MatchHelper;
import com.pj.squashrestapp.model.entityhelper.MatchStatus;
import com.pj.squashrestapp.model.entityhelper.MatchValidator;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MatchDto {

  @EqualsAndHashCode.Include
  private final Long matchId;
  private final PlayerDto firstPlayer;
  private final PlayerDto secondPlayer;

  private final Long roundGroupId;
  private final int roundGroupNumber;

  private final Long roundId;
  private final int roundNumber;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate roundDate;

  private final Long seasonId;
  private final int seasonNumber;

  private final List<SetDto> sets;

  private final MatchStatus status;

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

    this.status = new MatchValidator(match).checkStatus();
  }

  @Override
  public String toString() {
    return "[" + matchId + "] " + firstPlayer + " vs. " + secondPlayer + " : " + sets;
  }

}
