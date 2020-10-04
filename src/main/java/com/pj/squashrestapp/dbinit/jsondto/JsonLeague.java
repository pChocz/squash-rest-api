package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonLeague {

  private String name;

  private String logoBase64;

  private UUID uuid;

  @JsonProperty("hof")
  @SerializedName("hof")
  private ArrayList<JsonHallOfFameSeason> hallOfFameSeasons;

  private ArrayList<JsonSeason> seasons;

}
