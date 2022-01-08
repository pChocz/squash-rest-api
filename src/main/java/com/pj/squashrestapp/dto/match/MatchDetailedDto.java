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
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** */
@Getter
@NoArgsConstructor
public class MatchDetailedDto implements MatchDto {

  private UUID matchUuid;
  private PlayerDto firstPlayer;
  private PlayerDto secondPlayer;
  private PlayerDto winner;

  private int roundGroupNumber;

  private UUID roundUuid;
  private int roundNumber;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate date;

  private UUID seasonUuid;
  private int seasonNumber;

  private String leagueName;

  private List<SetDto> sets;

  private MatchStatus status;

  @JsonIgnore private MatchFormatType matchFormatType;
  @JsonIgnore private SetWinningType regularSetWinningType;
  @JsonIgnore private int regularSetWinningPoints;
  @JsonIgnore private SetWinningType tieBreakWinningType;
  @JsonIgnore private int tieBreakWinningPoints;

  public MatchDetailedDto(final Match match) {
    final RoundGroup roundGroup = match.getRoundGroup();
    final Round round = roundGroup.getRound();
    final Season season = round.getSeason();

    this.matchUuid = match.getUuid();
    this.firstPlayer = new PlayerDto(match.getFirstPlayer());
    this.secondPlayer = new PlayerDto(match.getSecondPlayer());
    this.roundGroupNumber = roundGroup.getNumber();
    this.date = round.getDate();
    this.roundUuid = round.getUuid();
    this.roundNumber = round.getNumber();
    this.seasonUuid = season.getUuid();
    this.seasonNumber = season.getNumber();
    this.leagueName = season.getLeague().getName();

    this.matchFormatType = match.getMatchFormatType();
    this.regularSetWinningType = match.getRegularSetWinningType();
    this.regularSetWinningPoints = match.getRegularSetWinningPoints();
    this.tieBreakWinningType = match.getTiebreakWinningType();
    this.tieBreakWinningPoints = match.getTiebreakWinningPoints();

    this.sets = new ArrayList<>();
    for (final SetResult setResult : match.getSetResults()) {
      this.sets.add(new SetDto(setResult, matchFormatType));
    }
    this.sets.sort(Comparator.comparingInt(SetDto::getSetNumber));

    this.status = MatchStatusHelper.checkStatus(this);
    this.winner = status == MatchStatus.FINISHED ? MatchStatusHelper.getWinner(this) : null;
  }

  @Override
  public String toString() {
    return "[" + matchUuid + "] " + firstPlayer + " vs. " + secondPlayer + " : " + sets;
  }
}
