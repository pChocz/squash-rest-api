package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
public class RoundDto {

  private final UUID roundUuid;
  private final int roundNumber;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private final LocalDate roundDate;

  private final boolean isFinished;

  public RoundDto(final Round round) {
    this.roundUuid = round.getUuid();
    this.roundNumber = round.getNumber();
    this.isFinished = round.isFinished();
    this.roundDate = round.getDate();
  }
}
