package com.pj.squashrestapp.dto.scoreboard.headtohead;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** */
@Getter
@NoArgsConstructor
public class HeadToHeadChartRow {

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate date;
  private int numberOfSets;
  private boolean firstPlayerWon;

  public HeadToHeadChartRow(
      final LocalDate date, final int numberOfSets, final boolean firstPlayerWon) {
    this.date = date;
    this.numberOfSets = numberOfSets;
    this.firstPlayerWon = firstPlayerWon;
  }
}
