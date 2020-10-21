package com.pj.squashrestapp.model.dto.playerroundsstats;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.RoundDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Slf4j
@Getter
public class PlayerRoundsStats {

  private final List<PlayerSingleRoundStats> singleRoundStats;

  public PlayerRoundsStats(final Player player, final League league, final ArrayListMultimap<String, Integer> xpPointsPerSplit) {
    this.singleRoundStats = new ArrayList<>();
    for (final Season season : league.getSeasons()) {
      for (final Round round : season.getRounds()) {
        final List<Integer> xpPoints = xpPointsPerSplit.get(round.getSplit());
        this.singleRoundStats.add(new PlayerSingleRoundStats(player, round, xpPoints));
      }
    }
  }

}
