package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonSeason {

  private int number;

  private UUID uuid;

  private String xpPointsType;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate startDate;

  private ArrayList<JsonBonusPoint> bonusPoints;

  private ArrayList<JsonRound> rounds;

}
