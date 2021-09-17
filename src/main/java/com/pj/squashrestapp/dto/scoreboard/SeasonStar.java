package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import lombok.Value;

@Value
public class SeasonStar {

  int roundNumber;
  String groupCharacter;
  Type type;

}
