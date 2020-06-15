package com.pj.squashrestapp.model.dto.scoreboard;

import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Getter
public class RoundGroupScoreboard {

  private final List<ScoreboardRow> scoreboardRows;

  private final Collection<MatchDto> matches;

  public RoundGroupScoreboard(final Collection<MatchDto> matches) {
    this.matches = matches;

    this.scoreboardRows = new ArrayList<>();

    for (final MatchDto match : matches) {
      final ScoreboardRow scoreboardRowFirst = getScoreboardRow(match.getFirstPlayer());
      scoreboardRowFirst.applyMatch(match);

      final ScoreboardRow scoreboardRowSecond = getScoreboardRow(match.getSecondPlayer());
      scoreboardRowSecond.applyMatch(match);
    }

    Collections.sort(scoreboardRows);
  }

  private ScoreboardRow getScoreboardRow(final PlayerDto player) {
    ScoreboardRow scoreboardRowFirst = scoreboardRows
            .stream()
            .filter(e -> e.getPlayer().equals(player))
            .findFirst()
            .orElse(null);

    if (scoreboardRowFirst == null) {
      scoreboardRowFirst = new ScoreboardRow(player);
      scoreboardRows.add(scoreboardRowFirst);
    }
    return scoreboardRowFirst;
  }

}
