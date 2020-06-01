package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.HallOfFameSeason;
import lombok.Value;

/**
 *
 */
@SuppressWarnings("unused")
@Value
public class HallOfFameSeasonDto {

  Long id;
  int seasonNumber;

  String league1stPlace;
  String league2ndPlace;
  String league3rdPlace;

  String cup1stPlace;
  String cup2ndPlace;
  String cup3rdPlace;

  String superCupWinner;

  public HallOfFameSeasonDto(final HallOfFameSeason hallOfFameSeason) {
    this.id = hallOfFameSeason.getId();
    this.seasonNumber = hallOfFameSeason.getSeasonNumber();
    this.league1stPlace = hallOfFameSeason.getLeague1stPlace();
    this.league2ndPlace = hallOfFameSeason.getLeague2ndPlace();
    this.league3rdPlace = hallOfFameSeason.getLeague3rdPlace();
    this.cup1stPlace = hallOfFameSeason.getCup1stPlace();
    this.cup2ndPlace = hallOfFameSeason.getCup2ndPlace();
    this.cup3rdPlace = hallOfFameSeason.getCup3rdPlace();
    this.superCupWinner = hallOfFameSeason.getSuperCupWinner();
  }

}
