package com.pj.squashrestapp.dto.playerroundsstats;

import com.pj.squashrestapp.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoundOpponent {

  private PlayerDto player;
  private boolean won;
  private int placeInGroup;
}
