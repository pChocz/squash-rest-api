package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class JsonLeague {

  private String name;
  private String logoBase64;
  private ArrayList<JsonHallOfFameSeason> hallOfFameSeasons;
  private ArrayList<JsonSeason> seasons;

}
