package com.pj.squashrestapp.model.dto.scoreboard;

import com.pj.squashrestapp.model.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.matchresulthelper.MatchStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Slf4j
@Getter
public class RoundGroupScoreboard {

  private final int roundGroupNumber;
  private final List<ScoreboardRow> scoreboardRows;
  private final Collection<MatchDetailedDto> matches;

  public RoundGroupScoreboard(final Collection<MatchDetailedDto> matches) {
    this.matches = matches;

    this.roundGroupNumber = matches
            .stream()
            .findFirst()
            .map(MatchDetailedDto::getRoundGroupNumber)
            .orElse(0);

    this.scoreboardRows = new ArrayList<>();

    for (final MatchDetailedDto match : matches) {
      final ScoreboardRow scoreboardRowFirst = getScoreboardRow(match.getFirstPlayer());
      final ScoreboardRow scoreboardRowSecond = getScoreboardRow(match.getSecondPlayer());

      if (match.getStatus() == MatchStatus.FINISHED) {
        scoreboardRowFirst.applyMatch(match);
        scoreboardRowSecond.applyMatch(match);
      }
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
