package com.pj.squashrestapp.dto.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchValidator;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalMatchType;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;

/** */
@Getter
public class AdditionalMatchSimpleDto implements MatchDto {

  private final PlayerDto firstPlayer;
  private final PlayerDto secondPlayer;
  private final AdditionalMatchType type;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private final LocalDate date;

  private final List<SetDto> sets;

  private final MatchStatus status;

  public AdditionalMatchSimpleDto(final AdditionalMatch match) {
    this.firstPlayer = new PlayerDto(match.getFirstPlayer());
    this.secondPlayer = new PlayerDto(match.getSecondPlayer());
    this.date = match.getDate();
    this.type = match.getType();

    this.sets = new ArrayList<>();
    for (final AdditionalSetResult setResult : match.getSetResults()) {
      this.sets.add(new SetDto(setResult));
    }
    this.sets.sort(Comparator.comparingInt(SetDto::getSetNumber));

    this.status = new MatchValidator(match).checkStatus();
  }

  @Override
  public String toString() {
    return firstPlayer + " vs. " + secondPlayer + " : " + sets;
  }
}
