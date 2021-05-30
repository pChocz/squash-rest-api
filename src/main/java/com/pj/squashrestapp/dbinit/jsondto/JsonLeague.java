package com.pj.squashrestapp.dbinit.jsondto;

import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetWinningType;
import java.util.ArrayList;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class JsonLeague {

  private String name;

  private String time;

  private String location;

  private String logoBase64;

  private UUID uuid;

  private MatchFormatType matchFormatType;

  private SetWinningType regularSetWinningType;

  private SetWinningType tiebreakWinningType;

  private int regularSetWinningPoints;

  private int tiebreakWinningPoints;

  private int numberOfRoundsPerSeason;

  private int roundsToBeDeducted;

  private ArrayList<JsonSeason> seasons;

  private ArrayList<JsonLeagueTrophy> trophies;

  private ArrayList<JsonLeagueRule> rules;

  private ArrayList<JsonAdditionalMatch> additionalMatches;
}
