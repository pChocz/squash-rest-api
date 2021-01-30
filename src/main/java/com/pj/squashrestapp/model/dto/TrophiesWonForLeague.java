package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.HallOfFameSeason;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
public class TrophiesWonForLeague {

  private final PlayerDto player;
  private final LeagueDtoSimple league;
  private final List<TrophiesWonForSeason> trophiesPerSeason;

  public TrophiesWonForLeague(final PlayerDto player,
                              final LeagueDtoSimple league,
                              final List<HallOfFameSeason> hallOfFameSeasons) {
    this.player = player;
    this.league = league;
    this.trophiesPerSeason = new ArrayList<>();

    for (final HallOfFameSeason hallOfFameSeason : hallOfFameSeasons) {
      final int seasonNumber = hallOfFameSeason.getSeasonNumber();
      final TrophiesWonForSeason trophiesWonForSeason = new TrophiesWonForSeason(seasonNumber);

      if (hallOfFameSeason.getLeague1stPlace().equals(player.getUsername())) {
        trophiesWonForSeason.addTrophy(Trophy.LEAGUE_1ST);
      }
      if (hallOfFameSeason.getLeague2ndPlace().equals(player.getUsername())) {
        trophiesWonForSeason.addTrophy(Trophy.LEAGUE_2ND);
      }
      if (hallOfFameSeason.getLeague3rdPlace().equals(player.getUsername())) {
        trophiesWonForSeason.addTrophy(Trophy.LEAGUE_3RD);
      }
      if (hallOfFameSeason.getCup1stPlace().equals(player.getUsername())) {
        trophiesWonForSeason.addTrophy(Trophy.CUP_1ST);
      }
      if (hallOfFameSeason.getCup2ndPlace().equals(player.getUsername())) {
        trophiesWonForSeason.addTrophy(Trophy.CUP_2ND);
      }
      if (hallOfFameSeason.getCup3rdPlace().equals(player.getUsername())) {
        trophiesWonForSeason.addTrophy(Trophy.CUP_3RD);
      }
      if (hallOfFameSeason.getSuperCupWinner().equals(player.getUsername())) {
        trophiesWonForSeason.addTrophy(Trophy.SUPER_CUP);
      }
      if (hallOfFameSeason.getPretendersCupWinner().equals(player.getUsername())) {
        trophiesWonForSeason.addTrophy(Trophy.PRETENDERS_CUP);
      }

      if (hallOfFameSeason.getCoviders() != null) {
        final List<String> seasonCoviders = List.of(hallOfFameSeason.getCoviders().split("\\|"));
        if (seasonCoviders.contains(player.getUsername())) {
          trophiesWonForSeason.addTrophy(Trophy.COVID);
        }
      }

      if (hallOfFameSeason.getAllRoundsAttendees() != null) {
        final List<String> seasonAllRoundsAttendees = List.of(hallOfFameSeason.getAllRoundsAttendees().split("\\|"));
        if (seasonAllRoundsAttendees.contains(player.getUsername())) {
          trophiesWonForSeason.addTrophy(Trophy.ALL_ROUNDS_ATTENDEE);
        }
      }

      this.trophiesPerSeason.add(trophiesWonForSeason);
    }

  }

  @Override
  public String toString() {
    return player + " | " + league + " | " + getNumberOfTrophies() + " trophies";
  }

  private int getNumberOfTrophies() {
    return trophiesPerSeason
            .stream()
            .mapToInt(t -> t.getTrophies().size())
            .sum();
  }

}
