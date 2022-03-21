package com.pj.squashrestapp.dto.playerseasonsstats;

import com.pj.squashrestapp.dto.PlayerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor
public class PlayerAllSeasonsStats {

  PlayerDto player;
  List<PlayerSingleSeasonStats> playerSingleSeasonStats;

  public PlayerAllSeasonsStats(final PlayerDto player) {
    this.player = player;
    this.playerSingleSeasonStats = new ArrayList<>();
  }

  public void addSingleSeasonStats(final PlayerSingleSeasonStats playerSingleSeasonStats) {
    this.playerSingleSeasonStats.add(playerSingleSeasonStats);
  }

}
