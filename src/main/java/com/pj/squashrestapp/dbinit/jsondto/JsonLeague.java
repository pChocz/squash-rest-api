package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonLeague {

  private String name;

  private String time;

  private String location;

  private String logoBase64;

  private UUID uuid;

  private ArrayList<JsonSeason> seasons;

  private ArrayList<JsonLeagueTrophy> trophies;

  private ArrayList<JsonLeagueRule> rules;

  private ArrayList<JsonAdditionalMatch> additionalMatches;

}
