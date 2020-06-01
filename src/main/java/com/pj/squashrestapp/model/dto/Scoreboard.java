package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Getter
public class Scoreboard {

  private final List<ScoreboardRow> scoreboardRows;
  private final Collection<MatchDto> matches;

  public Scoreboard(final Collection<MatchDto> matches) {
    this.matches = matches;

    this.scoreboardRows = new ArrayList<>();

    for (final MatchDto match : matches) {

      ScoreboardRow scoreboardRowFirst = scoreboardRows.stream().filter(e -> e.getPlayer().equals(match.getFirstPlayer())).findFirst().orElse(null);
      if (scoreboardRowFirst == null) {
        scoreboardRowFirst = new ScoreboardRow(match.getFirstPlayer());
        scoreboardRows.add(scoreboardRowFirst);
      }

      ScoreboardRow scoreboardRowSecond = scoreboardRows.stream().filter(e -> e.getPlayer().equals(match.getSecondPlayer())).findFirst().orElse(null);
      if (scoreboardRowSecond == null) {
        scoreboardRowSecond = new ScoreboardRow(match.getSecondPlayer());
        scoreboardRows.add(scoreboardRowSecond);
      }

      scoreboardRowFirst.applyMatch(match);
      scoreboardRowSecond.applyMatch(match);
    }

    Collections.sort(scoreboardRows);
  }


}
