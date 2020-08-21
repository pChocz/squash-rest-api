package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonHallOfFameSeason {

  private int seasonNumber;
  private String league1stPlace;
  private String league2ndPlace;
  private String league3rdPlace;
  private String cup1stPlace;
  private String cup2ndPlace;
  private String cup3rdPlace;
  private String superCupWinner;
  private String pretendersCupWinner;

}
