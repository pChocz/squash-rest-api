package com.pj.squashrestapp.dto.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.SetDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchValidator;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalMatchType;
import com.pj.squashrestapp.model.AdditonalSetResult;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Getter
public class AdditionalMatchDetailedDto implements MatchDto {

  private final UUID matchUuid;
  private final PlayerDto firstPlayer;
  private final PlayerDto secondPlayer;
  private final UUID leagueUuid;
  private final AdditionalMatchType type;
  private final int seasonNumber;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private final LocalDate date;

  private final List<SetDto> sets;

  private final MatchStatus status;

  public AdditionalMatchDetailedDto(final AdditionalMatch match) {
    this.matchUuid = match.getUuid();
    this.firstPlayer = new PlayerDto(match.getFirstPlayer());
    this.secondPlayer = new PlayerDto(match.getSecondPlayer());
    this.leagueUuid = match.getLeague().getUuid();
    this.date = match.getDate();
    this.type = match.getType();
    this.seasonNumber = match.getSeasonNumber();

    this.sets = new ArrayList<>();
    for (final AdditonalSetResult setResult : match.getSetResults()) {
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
