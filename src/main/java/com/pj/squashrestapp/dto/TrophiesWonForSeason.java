package com.pj.squashrestapp.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 *
 */
@Getter
public class TrophiesWonForSeason {

  private final int seasonNumber;
  private final List<Trophy> trophies;

  public TrophiesWonForSeason(final int seasonNumber) {
    this.seasonNumber = seasonNumber;
    this.trophies = new ArrayList<>();
  }

  public void addTrophy(final Trophy trophy) {
    this.trophies.add(trophy);
  }

}
