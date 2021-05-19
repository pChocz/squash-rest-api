package com.pj.squashrestapp.dto.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.SetDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchValidator;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class MatchSimpleDto implements MatchDto {

  private final PlayerDto firstPlayer;
  private final PlayerDto secondPlayer;
  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private final LocalDate date;
  private final List<SetDto> sets;
  private final MatchStatus status;

  public MatchSimpleDto(final Match match) {
    final RoundGroup roundGroup = match.getRoundGroup();
    final Round round = roundGroup.getRound();

    this.firstPlayer = new PlayerDto(match.getFirstPlayer());
    this.secondPlayer = new PlayerDto(match.getSecondPlayer());
    this.date = round.getDate();

    this.sets = new ArrayList<>();
    for (final SetResult setResult : match.getSetResults().stream().sorted().collect(Collectors.toList())) {
      this.sets.add(new SetDto(setResult));
    }

    this.status = new MatchValidator(match).checkStatus();
  }

  @Override
  public String toString() {
    return firstPlayer + " vs. " + secondPlayer + " : " + sets;
  }

}
