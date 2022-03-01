package com.pj.squashrestapp.dto.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatusHelper;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/** */
@Getter
public class MatchSimpleDto implements MatchDto {

  private final PlayerDto firstPlayer;
  private final PlayerDto secondPlayer;
  private final PlayerDto winner;
  private final List<SetDto> sets;
  private final MatchStatus status;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private final LocalDate date;

  @JsonIgnore private final MatchFormatType matchFormatType;
  @JsonIgnore private final SetWinningType regularSetWinningType;
  @JsonIgnore private final int regularSetWinningPoints;
  @JsonIgnore private final SetWinningType tieBreakWinningType;
  @JsonIgnore private final int tieBreakWinningPoints;

  public MatchSimpleDto(final Match match) {
    final RoundGroup roundGroup = match.getRoundGroup();
    final Round round = roundGroup.getRound();

    this.firstPlayer = new PlayerDto(match.getFirstPlayer());
    this.secondPlayer = new PlayerDto(match.getSecondPlayer());
    this.date = round.getDate();

    this.matchFormatType = match.getMatchFormatType();
    this.regularSetWinningType = match.getRegularSetWinningType();
    this.regularSetWinningPoints = match.getRegularSetWinningPoints();
    this.tieBreakWinningType = match.getTiebreakWinningType();
    this.tieBreakWinningPoints = match.getTiebreakWinningPoints();

    this.sets = new ArrayList<>();
    for (final SetResult setResult :
        match.getSetResults().stream().sorted().collect(Collectors.toList())) {
      this.sets.add(new SetDto(setResult, matchFormatType));
    }

    this.status = MatchStatusHelper.checkStatus(this);
    this.winner = status == MatchStatus.FINISHED ? MatchStatusHelper.getWinner(this) : null;
  }

  /** This one is only for testing purposes. */
  public MatchSimpleDto(
      final MatchFormatType matchFormatType,
      final SetWinningType regularSetWinningType,
      final SetWinningType tieBreakWinningType,
      final int regularSetWinningPoints,
      final int tieBreakWinningPoints,
      final String... setResults) {
    this.firstPlayer = null;
    this.secondPlayer = null;
    this.date = LocalDate.now();

    this.matchFormatType = matchFormatType;
    this.regularSetWinningType = regularSetWinningType;
    this.regularSetWinningPoints = regularSetWinningPoints;
    this.tieBreakWinningType = tieBreakWinningType;
    this.tieBreakWinningPoints = tieBreakWinningPoints;

    this.sets = new ArrayList<>();
    int setNumber = 1;
    for (final String setResultAsString : setResults) {
      final SetDto setDto = new SetDto(setResultAsString);
      setDto.setSetNumber(setNumber);
      this.sets.add(setDto);
      setNumber++;
    }
    this.status = MatchStatusHelper.checkStatus(this);
    this.winner = status == MatchStatus.FINISHED ? MatchStatusHelper.getWinner(this) : null;
  }

  @Override
  public boolean checkFinished() {
    return this.getStatus() == MatchStatus.FINISHED;
  }

  @Override
  public String toString() {
    return firstPlayer + " vs. " + secondPlayer + " : " + sets;
  }
}
