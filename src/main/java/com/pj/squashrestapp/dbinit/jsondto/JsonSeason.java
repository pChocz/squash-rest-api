package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonSeason {

  private int number;

  private UUID uuid;

  private String xpPointsType;

  private String description;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate startDate;

  private ArrayList<JsonBonusPoint> bonusPoints;

  private ArrayList<JsonRound> rounds;
}
