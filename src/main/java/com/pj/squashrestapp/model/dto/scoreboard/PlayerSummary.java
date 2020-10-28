package com.pj.squashrestapp.model.dto.scoreboard;

import lombok.Value;

@Value
public class PlayerSummary {

  ScoreboardRow scoreboardRow;
  int leagues;
  int seasons;
  int rounds;

}
