package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
@NoArgsConstructor
public class RoundDto {

  private UUID roundUuid;
  private int roundNumber;
  private int seasonNumber;
  private String split;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate roundDate;

  private boolean finished;

  public RoundDto(final Round round) {
    this.roundUuid = round.getUuid();
    this.roundNumber = round.getNumber();
    this.finished = round.isFinished();
    this.roundDate = round.getDate();
    this.seasonNumber = round.getSeason().getNumber();
    this.split = round.getSplit();
  }

  public RoundDto(final RoundScoreboard roundScoreboard) {
    this.roundUuid = roundScoreboard.getRoundUuid();
    this.roundNumber = roundScoreboard.getRoundNumber();
    this.finished = roundScoreboard.isFinishedState();
    this.roundDate = roundScoreboard.getRoundDate();
    this.seasonNumber = roundScoreboard.getSeasonNumber();
    this.split = roundScoreboard.getSplit();
  }
}
