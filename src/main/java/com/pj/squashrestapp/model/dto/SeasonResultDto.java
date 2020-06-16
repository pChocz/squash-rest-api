package com.pj.squashrestapp.model.dto;

import lombok.Value;

import java.util.Collection;

/**
 *
 */
@Value
public class SeasonResultDto {

  Long id;
  Long seasonId;
  Long playerId;
  Collection<Integer> pointsPerRound;
  int bonusPoints;

}
