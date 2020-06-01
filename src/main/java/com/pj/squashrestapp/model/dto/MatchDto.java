package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unused")
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

  public MatchDto(final List<SingleSetRowDto> singleSetRowsDto) {
    this.matchId = singleSetRowsDto.get(0).getMatchId();
    this.firstPlayer = new PlayerDto(singleSetRowsDto.get(0).getFirstPlayerId(), singleSetRowsDto.get(0).getFirstPlayerName());
    this.secondPlayer = new PlayerDto(singleSetRowsDto.get(0).getSecondPlayerId(), singleSetRowsDto.get(0).getSecondPlayerName());
    this.roundGroupId = singleSetRowsDto.get(0).getRoundGroupId();
    this.roundGroupNumber = singleSetRowsDto.get(0).getRoundGroupNumber();
    this.roundDate = singleSetRowsDto.get(0).getRoundDate();
    this.roundId = singleSetRowsDto.get(0).getRoundId();
    this.roundNumber = singleSetRowsDto.get(0).getRoundNumber();
    this.seasonId = singleSetRowsDto.get(0).getSeasonId();
    this.seasonNumber = singleSetRowsDto.get(0).getSeasonNumber();

    this.sets = new ArrayList<>();
    for (final SingleSetRowDto setRowDto : singleSetRowsDto) {
      this.sets.add(new SetDto(setRowDto));
    }
  }

}
