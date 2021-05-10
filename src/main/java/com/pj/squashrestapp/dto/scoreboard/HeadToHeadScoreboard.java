package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.aspects.LoggableQuery;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.SetDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class HeadToHeadScoreboard implements LoggableQuery {

  private final int numberOfMatches;
  private int numberOfRegularMatches;
  private int numberOfTiebreaks;
  private final HeadToHeadScoreboardRow winner;
  private final HeadToHeadScoreboardRow looser;
  private final Collection<MatchDto> matches;

  public HeadToHeadScoreboard(final Collection<MatchDto> matches) {
    this.matches = matches;
    this.numberOfMatches = matches.size();

    if (numberOfMatches == 0) {
      winner = null;
      looser = null;
      return;
    }

    final Map<PlayerDto, Map<Integer, Integer>> winningSetsPerPlayer = new HashMap<>();
    winningSetsPerPlayer.put(matches.stream().findFirst().get().getFirstPlayer(), new HashMap<>());
    winningSetsPerPlayer.put(matches.stream().findFirst().get().getSecondPlayer(), new HashMap<>());

    final List<PlayersStatsScoreboardRow> scoreboardRows = new ArrayList<>();
    for (final MatchDto match : getSortedMatches(matches)) {
      final PlayersStatsScoreboardRow scoreboardRowFirst = getScoreboardRowOrBuildNew(scoreboardRows, match.getFirstPlayer());
      scoreboardRowFirst.applyMatch(match);

      final PlayersStatsScoreboardRow scoreboardRowSecond = getScoreboardRowOrBuildNew(scoreboardRows, match.getSecondPlayer());
      scoreboardRowSecond.applyMatch(match);

      final PlayerDto firstPlayer = match.getFirstPlayer();
      final PlayerDto secondPlayer = match.getSecondPlayer();

      for (final SetDto set : match.getSets()) {
        if (set.getFirstPlayerScoreNullSafe() > set.getSecondPlayerScoreNullSafe()) {
          winningSetsPerPlayer.get(firstPlayer).merge(set.getSetNumber(), 1, Integer::sum);

        } else if (set.getFirstPlayerScoreNullSafe() < set.getSecondPlayerScoreNullSafe()) {
          winningSetsPerPlayer.get(secondPlayer).merge(set.getSetNumber(), 1, Integer::sum);
        }
      }
    }
    Collections.sort(scoreboardRows);

    this.numberOfTiebreaks =
            winningSetsPerPlayer.get(matches.stream().findFirst().get().getFirstPlayer()).getOrDefault(3, 0)
            + winningSetsPerPlayer.get(matches.stream().findFirst().get().getSecondPlayer()).getOrDefault(3, 0);

    this.numberOfRegularMatches = numberOfMatches - numberOfTiebreaks;

    winner = new HeadToHeadScoreboardRow(scoreboardRows.get(0), winningSetsPerPlayer);
    looser = new HeadToHeadScoreboardRow(scoreboardRows.get(1), winningSetsPerPlayer);
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
    if (winner != null && looser != null) {
      return "h2h: " + winner.getPlayer() + " v. " + looser.getPlayer();
    } else {
      return "h2h: EMPTY";
    }
  }

  @Override
  public String message() {
    return toString();
  }

}
