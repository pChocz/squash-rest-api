package com.pj.squashrestapp.dto.leaguestats;

import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetWinningType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** */
@Builder
@Getter
@AllArgsConstructor
public class OveralStats {

  private final String leagueName;
  private final UUID leagueUuid;
  private final String time;
  private final String location;
  private final int seasons;
  private final int players;
  private final BigDecimal averagePlayersPerRound;
  private final BigDecimal averagePlayersPerGroup;
  private final BigDecimal averageGroupsPerRound;
  private final int rounds;
  private final int matches;
  private final int sets;
  private final int points;
  private final MatchFormatType matchFormatType;
  private final SetWinningType regularSetWinningType;
  private final SetWinningType tiebreakWinningType;
  private final int regularSetWinningPoints;
  private final int tiebreakWinningPoints;
  private final int numberOfRoundsPerSeason;
  private final int roundsToBeDeducted;
  private final LocalDate dateOfCreation;
}
