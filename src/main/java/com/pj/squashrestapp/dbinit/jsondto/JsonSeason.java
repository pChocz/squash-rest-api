package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class JsonSeason {

  private int number;
  private String startDate;
  private ArrayList<JsonBonusPoint> bonusPoints;
  private ArrayList<JsonRound> rounds;

}
