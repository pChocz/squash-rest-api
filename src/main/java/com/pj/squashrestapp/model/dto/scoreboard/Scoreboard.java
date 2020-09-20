package com.pj.squashrestapp.model.dto.scoreboard;

import com.pj.squashrestapp.model.dto.match.MatchDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class Scoreboard {

  private final int numberOfMatches;
  private final List<ScoreboardRow> scoreboardRows;
  private List<MatchDto> matches;

  public Scoreboard(final Collection<MatchDto> matches) {
    this.matches = getSortedMatches(matches);
    this.numberOfMatches = matches.size();
    this.scoreboardRows = new ArrayList<>();

    for (final MatchDto match : matches) {
      final ScoreboardRow scoreboardRowFirst = getScoreboardRowOrBuildNew(match.getFirstPlayer());
      scoreboardRowFirst.applyMatch(match);

      final ScoreboardRow scoreboardRowSecond = getScoreboardRowOrBuildNew(match.getSecondPlayer());
      scoreboardRowSecond.applyMatch(match);
    }
    Collections.sort(scoreboardRows);
  }

  private List<MatchDto> getSortedMatches(final Collection<MatchDto> matches) {
    return matches
            .stream()
            .sorted(Comparator
                    .comparing(MatchDto::getRoundDate)
                    .reversed())
            .collect(Collectors.toList());
  }

  private ScoreboardRow getScoreboardRowOrBuildNew(final PlayerDto player) {
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

  public void makeItSinglePlayerScoreboard(final Long playerId) {
    final Iterator<ScoreboardRow> iterator = this.scoreboardRows.iterator();
    while (iterator.hasNext()) {
      final ScoreboardRow scoreboardRow = iterator.next();
      final Long currentPlayerId = scoreboardRow.getPlayer().getId();
      if (currentPlayerId != playerId) {
        iterator.remove();
      }
    }
  }

  public void removeMatches() {
    this.matches = null;
  }

}
