package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.TrophyForLeague;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
                              final List<TrophyForLeague> trophiesForLeague) {
    this.player = player;
    this.league = league;
    this.trophiesPerSeason = new ArrayList<>();

    final List<Integer> listOfSeasonNumbers = trophiesForLeague
            .stream()
            .map(TrophyForLeague::getSeasonNumber)
            .distinct()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

    for (final int seasonNumber : listOfSeasonNumbers) {
      final TrophiesWonForSeason trophiesWonForSeason = new TrophiesWonForSeason(seasonNumber);

      final List<TrophyForLeague> seasonTrophies = trophiesForLeague
              .stream()
              .filter(trophyForLeague -> trophyForLeague.getSeasonNumber() == seasonNumber)
              .sorted(Comparator.comparingInt(o -> o.getTrophy().ordinal()))
              .collect(Collectors.toList());

      for (final TrophyForLeague trophyForLeague : seasonTrophies) {
        trophiesWonForSeason.addTrophy(trophyForLeague.getTrophy());
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
