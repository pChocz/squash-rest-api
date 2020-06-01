package com.pj.squashrestapp.model.dto;

import lombok.Value;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unused")
@Value
public class SeasonResultDto {

  Long id;
  Long seasonId;


//  int roundsPlayed;
//  int roundsCounted;

  Long playerId;

  Collection<Integer> pointsPerRound;
  int bonusPoints;

}
