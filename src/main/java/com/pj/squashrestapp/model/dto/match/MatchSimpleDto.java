package com.pj.squashrestapp.model.dto.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.SetDto;
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
  private final LocalDate roundDate;
  private final List<SetDto> sets;

  public MatchSimpleDto(final Match match) {
    final RoundGroup roundGroup = match.getRoundGroup();
    final Round round = roundGroup.getRound();

    this.firstPlayer = new PlayerDto(match.getFirstPlayer());
    this.secondPlayer = new PlayerDto(match.getSecondPlayer());
    this.roundDate = round.getDate();

    this.sets = new ArrayList<>();
    for (final SetResult setResult : match.getSetResults().stream().sorted().collect(Collectors.toList())) {
      this.sets.add(new SetDto(setResult));
    }
  }

}
