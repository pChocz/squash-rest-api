package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.aspects.LoggableQuery;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class HeadToHeadScoreboard implements LoggableQuery {

  private final int numberOfMatches;
  private final HeadToHeadScoreboardRow winner;
  private final HeadToHeadScoreboardRow looser;
  private final Collection<MatchDto> matches;

  public HeadToHeadScoreboard(final Collection<MatchDto> matches) {
    this.numberOfMatches = matches.size();
    this.matches = matches;

    final List<PlayersStatsScoreboardRow> scoreboardRows = new ArrayList<>();
    for (final MatchDto match : getSortedMatches(matches)) {
      final PlayersStatsScoreboardRow scoreboardRowFirst = getScoreboardRowOrBuildNew(scoreboardRows, match.getFirstPlayer());
      scoreboardRowFirst.applyMatch(match);

      final PlayersStatsScoreboardRow scoreboardRowSecond = getScoreboardRowOrBuildNew(scoreboardRows, match.getSecondPlayer());
      scoreboardRowSecond.applyMatch(match);
    }
    Collections.sort(scoreboardRows);

    if (numberOfMatches == 0) {
      winner = null;
      looser = null;
    } else {
      winner = new HeadToHeadScoreboardRow(scoreboardRows.get(0));
      looser = new HeadToHeadScoreboardRow(scoreboardRows.get(1));
    }
  }

  private List<MatchDto> getSortedMatches(final Collection<MatchDto> matches) {
    return matches
            .stream()
            .sorted(Comparator
                    .comparing(MatchDto::getDate)
                    .reversed())
            .collect(Collectors.toList());
  }

  private PlayersStatsScoreboardRow getScoreboardRowOrBuildNew(final List<PlayersStatsScoreboardRow> scoreboardRows, final PlayerDto player) {
    PlayersStatsScoreboardRow scoreboardRowFirst = scoreboardRows
            .stream()
            .filter(e -> e.getPlayer().equals(player))
            .findFirst()
            .orElse(null);

    if (scoreboardRowFirst == null) {
      scoreboardRowFirst = new PlayersStatsScoreboardRow(player);
      scoreboardRows.add(scoreboardRowFirst);
    }
    return scoreboardRowFirst;
  }

  @Override
  public String toString() {
    return "h2h: " + winner.getPlayer() + " v. " + looser.getPlayer();
  }

  @Override
  public String message() {
    return toString();
  }

}
