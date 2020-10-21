package com.pj.squashrestapp.model.dto.playerroundsstats;

import com.pj.squashrestapp.model.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
@AllArgsConstructor
public class RoundOpponent {

  final PlayerDto player;
  final boolean won;
  final int placeInGroup;

}
