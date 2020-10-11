package com.pj.squashrestapp.service;

import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.LeagueDtoSimple;
import com.pj.squashrestapp.model.dto.PlayerDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
@Getter
public class PerLeagueMatchStats {

  private final long matches;
  private final LeagueDtoSimple league;
  private final List<PerPlayerMatchStats> perPlayer;

  public PerLeagueMatchStats(final League league, final AtomicLongMap<Player> atomicLongMap) {
    this.league = new LeagueDtoSimple(league);
    this.perPlayer = new ArrayList<>();
    this.matches = atomicLongMap.sum();
    for (final Player player : atomicLongMap.asMap().keySet()) {
      final PlayerDto playerDto = new PlayerDto(player);
      this.perPlayer.add(new PerPlayerMatchStats(atomicLongMap.get(player), playerDto));
    }
    this.perPlayer.sort(Comparator.comparingLong(PerPlayerMatchStats::getMatches).reversed());
  }

}
