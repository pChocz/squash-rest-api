package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate startDate;

}
