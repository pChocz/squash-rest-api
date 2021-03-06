package com.pj.squashrestapp.dto.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchValidator;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

/** */
@Getter
public class MatchDetailedDto implements MatchDto {

  private final UUID matchUuid;
  private final PlayerDto firstPlayer;
  private final PlayerDto secondPlayer;

  private final int roundGroupNumber;

  private final UUID roundUuid;
  private final int roundNumber;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private final LocalDate date;

  private final UUID seasonUuid;
  private final int seasonNumber;

  private final String leagueName;

  private final List<SetDto> sets;

  private final MatchStatus status;

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

    this.sets = new ArrayList<>();
    for (final SetResult setResult : match.getSetResults()) {
      this.sets.add(new SetDto(setResult));
    }
    this.sets.sort(Comparator.comparingInt(SetDto::getSetNumber));

    this.status = new MatchValidator(match).checkStatus();
  }

  @Override
  public String toString() {
    return "[" + matchUuid + "] " + firstPlayer + " vs. " + secondPlayer + " : " + sets;
  }
}
