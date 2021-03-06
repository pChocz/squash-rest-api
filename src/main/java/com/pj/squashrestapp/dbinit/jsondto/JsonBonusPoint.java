package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonBonusPoint {

  private UUID uuid;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate date;

  private UUID winner;

  private UUID looser;

  private int points;
}
