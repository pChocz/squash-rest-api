package com.pj.squashrestapp.model.dto.leaguestats;

import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.model.dto.Trophy;
import lombok.Getter;

import java.util.List;

/**
 *
 */
@Getter
public class LeagueSeasonTrophies {

  private final int seasonNumber;

  private final String league1stPlace;
  private final String league2ndPlace;
  private final String league3rdPlace;

  private final String cup1stPlace;
  private final String cup2ndPlace;
  private final String cup3rdPlace;

  private final String superCupWinner;
  private final String pretendersCupWinner;

  public LeagueSeasonTrophies(final int seasonNumber, final List<TrophyForLeague> trophiesForLeagueForSeason) {
    this.seasonNumber = seasonNumber;
    this.league1stPlace = getByTrophy(trophiesForLeagueForSeason, Trophy.LEAGUE_1ST);
    this.league2ndPlace = getByTrophy(trophiesForLeagueForSeason, Trophy.LEAGUE_2ND);
    this.league3rdPlace = getByTrophy(trophiesForLeagueForSeason, Trophy.LEAGUE_3RD);
    this.cup1stPlace = getByTrophy(trophiesForLeagueForSeason, Trophy.CUP_1ST);
    this.cup2ndPlace = getByTrophy(trophiesForLeagueForSeason, Trophy.CUP_2ND);
    this.cup3rdPlace = getByTrophy(trophiesForLeagueForSeason, Trophy.CUP_3RD);
    this.superCupWinner = getByTrophy(trophiesForLeagueForSeason, Trophy.SUPER_CUP);
    this.pretendersCupWinner = getByTrophy(trophiesForLeagueForSeason, Trophy.PRETENDERS_CUP);
  }

  private String getByTrophy(final List<TrophyForLeague> trophiesForLeagueForSeason, final Trophy trophy) {
    return trophiesForLeagueForSeason
            .stream()
            .filter(trophyForLeague -> trophyForLeague.getTrophy() == trophy)
            .findFirst()
            .map(trophyForLeague -> trophyForLeague
                    .getPlayer()
                    .getUsername())
            .orElse(" -- ");
  }

}
