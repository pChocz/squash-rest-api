package com.pj.squashrestapp.dto.playerroundsstats;

import com.pj.squashrestapp.dto.PlayerDto;
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
