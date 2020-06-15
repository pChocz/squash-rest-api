package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.RoundGroup;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class RoundScoreboard {

  @JsonIgnore
  private final List<Integer> playersPerGroup;

  private final List<Scoreboard> roundGroupScoreboards;

  public RoundScoreboard() {
    this.roundGroupScoreboards = new ArrayList<>();
    this.playersPerGroup = new ArrayList<>();
  }

  public void addRoundGroupNew(final RoundGroup roundGroup) {
    final List<MatchDto> matchDtos = roundGroup
            .getMatches()
            .stream()
            .map(MatchDto::new)
            .collect(Collectors.toList());
    final Scoreboard scoreboard = new Scoreboard(matchDtos);
    roundGroupScoreboards.add(scoreboard);
    playersPerGroup.add(scoreboard.getScoreboardRows().size());
  }

  public void addRoundGroup(final Collection<MatchDto> matches) {
    final Scoreboard scoreboard = new Scoreboard(matches);
    roundGroupScoreboards.add(scoreboard);
    playersPerGroup.add(scoreboard.getScoreboardRows().size());
  }

  public void assignPointsAndPlaces(final List<Integer> xpPoints) {
    int i = 0;
    for (final Scoreboard scoreboard : roundGroupScoreboards) {
      int j = 1;
      for (final ScoreboardRow scoreboardRow : scoreboard.getScoreboardRows()) {
        scoreboardRow.setPlaceInRound(i+1);
        scoreboardRow.setPlaceInGroup(j++);
        scoreboardRow.setXpEarned(xpPoints.get(i++));
      }
    }
  }

}
