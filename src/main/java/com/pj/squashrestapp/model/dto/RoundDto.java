package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 *
 */
@Slf4j
@Getter
public class RoundDto {

  private final Long roundId;
  private final int roundNumber;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate roundDate;
  private final boolean isFinished;

  public RoundDto(final Round round) {
    this.roundId = round.getId();
    this.roundNumber = round.getNumber();
    this.isFinished = round.isFinished();
    this.roundDate = round.getDate();
  }

}
