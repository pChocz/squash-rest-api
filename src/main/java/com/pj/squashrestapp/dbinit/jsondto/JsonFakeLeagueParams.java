package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonFakeLeagueParams {

  private String leagueName;
  private String logoBase64;
  private String xpPointsType;

  private int numberOfCompletedSeasons;
  private int numberOfRoundsInLastSeason;
  private int numberOfAllPlayers;
  private int minNumberOfAttendingPlayers;
  private int maxNumberOfAttendingPlayers;

  private int numberOfRoundsPerSeason;
  private int numberOfRoundsToBeDeducted;
  private MatchFormatType matchFormatType;
  private SetWinningType regularSetWinningType;
  private int regularSetWinningPoints;
  private SetWinningType tiebreakWinningType;
  private int tiebreakWinningPoints;
  private String when;
  private String where;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate startDate;
}
