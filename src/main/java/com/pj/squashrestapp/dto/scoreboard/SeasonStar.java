package com.pj.squashrestapp.dto.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonStar {

  private int roundNumber;
  private String groupCharacter;
  private Type type;

}
