package com.pj.squashrestapp.dto.scoreboard.headtohead;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.dto.match.SetDto;
import com.pj.squashrestapp.dto.scoreboard.PlayersStatsScoreboardRow;
import com.pj.squashrestapp.dto.setresultshistogram.ReadySetResultsHistogram;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

/** */
@Getter
@NoArgsConstructor
public class HeadToHeadScoreboard {

    private int numberOfMatches;
    private HeadToHeadScoreboardRow winner;
    private HeadToHeadScoreboardRow looser;
    private Collection<MatchDto> matches;
    private List<HeadToHeadChartRow> chartRows;
    private ReadySetResultsHistogram setResultsHistogram;

    public HeadToHeadScoreboard(final Collection<MatchDto> matches, final ReadySetResultsHistogram setResultsHistogram) {
        this.matches = matches;
        this.numberOfMatches = matches.size();
        this.setResultsHistogram = setResultsHistogram;

        if (numberOfMatches == 0) {
            winner = null;
            looser = null;
            chartRows = new ArrayList<>();
            return;
        }

        final Map<PlayerDto, Map<Integer, Integer>> winningSetsPerPlayer = new HashMap<>();
        winningSetsPerPlayer.put(matches.stream().findFirst().get().getFirstPlayer(), new HashMap<>());
        winningSetsPerPlayer.put(matches.stream().findFirst().get().getSecondPlayer(), new HashMap<>());

        final Map<PlayerDto, Map<Integer, Integer>> winningMatchesPerPlayer = new HashMap<>();
        winningMatchesPerPlayer.put(matches.stream().findFirst().get().getFirstPlayer(), new HashMap<>());
        winningMatchesPerPlayer.put(matches.stream().findFirst().get().getSecondPlayer(), new HashMap<>());

        final List<PlayersStatsScoreboardRow> scoreboardRows = new ArrayList<>();
        final Map<MatchDto, PlayerDto> matchWinnersMap = new LinkedHashMap<>();

        for (final MatchDto match : getSortedMatches(matches)) {
            final PlayersStatsScoreboardRow scoreboardRowFirst =
                    getScoreboardRowOrBuildNew(scoreboardRows, match.getFirstPlayer());
            scoreboardRowFirst.applyMatch(match);

            final PlayersStatsScoreboardRow scoreboardRowSecond =
                    getScoreboardRowOrBuildNew(scoreboardRows, match.getSecondPlayer());
            scoreboardRowSecond.applyMatch(match);

            final PlayerDto firstPlayer = match.getFirstPlayer();
            final PlayerDto secondPlayer = match.getSecondPlayer();

            int firstPlayerSetsWon = 0;
            int secondPlayerSetsWon = 0;

            for (final SetDto set : match.getSets()) {
                if (set.getFirstPlayerScoreNullSafe() > set.getSecondPlayerScoreNullSafe()) {
                    firstPlayerSetsWon++;
                    winningSetsPerPlayer.get(firstPlayer).merge(set.getSetNumber(), 1, Integer::sum);

                } else if (set.getFirstPlayerScoreNullSafe() < set.getSecondPlayerScoreNullSafe()) {
                    secondPlayerSetsWon++;
                    winningSetsPerPlayer.get(secondPlayer).merge(set.getSetNumber(), 1, Integer::sum);
                }
            }

            // One set (ONE_GAME)
            if (firstPlayerSetsWon == 1 && secondPlayerSetsWon == 0) {
                winningMatchesPerPlayer.get(firstPlayer).merge(1, 1, Integer::sum);
                matchWinnersMap.put(match, firstPlayer);

            } else if (firstPlayerSetsWon == 0 && secondPlayerSetsWon == 1) {
                winningMatchesPerPlayer.get(secondPlayer).merge(1, 1, Integer::sum);
                matchWinnersMap.put(match, secondPlayer);
            }

            // Two sets (BEST_OF_3)
            else if (firstPlayerSetsWon == 2 && secondPlayerSetsWon == 0) {
                winningMatchesPerPlayer.get(firstPlayer).merge(2, 1, Integer::sum);
                matchWinnersMap.put(match, firstPlayer);

            } else if (firstPlayerSetsWon == 0 && secondPlayerSetsWon == 2) {
                winningMatchesPerPlayer.get(secondPlayer).merge(2, 1, Integer::sum);
                matchWinnersMap.put(match, secondPlayer);
            }

            // Three sets (BEST_OF_3 or BEST_OF_5)
            else if (firstPlayerSetsWon == 2 && secondPlayerSetsWon == 1) {
                winningMatchesPerPlayer.get(firstPlayer).merge(3, 1, Integer::sum);
                matchWinnersMap.put(match, firstPlayer);

            } else if (firstPlayerSetsWon == 1 && secondPlayerSetsWon == 2) {
                winningMatchesPerPlayer.get(secondPlayer).merge(3, 1, Integer::sum);
                matchWinnersMap.put(match, secondPlayer);

            } else if (firstPlayerSetsWon == 3 && secondPlayerSetsWon == 0) {
                winningMatchesPerPlayer.get(firstPlayer).merge(3, 1, Integer::sum);
                matchWinnersMap.put(match, firstPlayer);

            } else if (firstPlayerSetsWon == 0 && secondPlayerSetsWon == 3) {
                winningMatchesPerPlayer.get(secondPlayer).merge(3, 1, Integer::sum);
                matchWinnersMap.put(match, secondPlayer);
            }

            // Four sets (BEST_OF_5)
            else if (firstPlayerSetsWon == 3 && secondPlayerSetsWon == 1) {
                winningMatchesPerPlayer.get(firstPlayer).merge(4, 1, Integer::sum);
                matchWinnersMap.put(match, firstPlayer);

            } else if (firstPlayerSetsWon == 1 && secondPlayerSetsWon == 3) {
                winningMatchesPerPlayer.get(secondPlayer).merge(4, 1, Integer::sum);
                matchWinnersMap.put(match, secondPlayer);
            }

            // Five sets (BEST_OF_5)
            else if (firstPlayerSetsWon == 3 && secondPlayerSetsWon == 2) {
                winningMatchesPerPlayer.get(firstPlayer).merge(5, 1, Integer::sum);
                matchWinnersMap.put(match, firstPlayer);

            } else if (firstPlayerSetsWon == 2 && secondPlayerSetsWon == 3) {
                winningMatchesPerPlayer.get(secondPlayer).merge(5, 1, Integer::sum);
                matchWinnersMap.put(match, secondPlayer);
            }
        }
        Collections.sort(scoreboardRows);

        winner = new HeadToHeadScoreboardRow(scoreboardRows.get(0), winningSetsPerPlayer, winningMatchesPerPlayer);
        looser = new HeadToHeadScoreboardRow(scoreboardRows.get(1), winningSetsPerPlayer, winningMatchesPerPlayer);

        this.chartRows = construct(matchWinnersMap, winner.getPlayer());
    }

    private List<MatchDto> getSortedMatches(final Collection<MatchDto> matches) {
        return matches.stream()
                .sorted(Comparator.comparing(MatchDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    private PlayersStatsScoreboardRow getScoreboardRowOrBuildNew(
            final List<PlayersStatsScoreboardRow> scoreboardRows, final PlayerDto player) {
        PlayersStatsScoreboardRow scoreboardRowFirst = scoreboardRows.stream()
                .filter(e -> e.getPlayer().equals(player))
                .findFirst()
                .orElse(null);

        if (scoreboardRowFirst == null) {
            scoreboardRowFirst = new PlayersStatsScoreboardRow(player);
            scoreboardRows.add(scoreboardRowFirst);
        }
        return scoreboardRowFirst;
    }

    private List<HeadToHeadChartRow> construct(
            final Map<MatchDto, PlayerDto> matchWinnersMap, final PlayerDto statsWinner) {
        final List<HeadToHeadChartRow> rows = new ArrayList<>();
        final ListIterator<MatchDto> iterator =
                new ArrayList<>(matchWinnersMap.keySet()).listIterator(matchWinnersMap.size());
        while (iterator.hasPrevious()) {
            final MatchDto match = iterator.previous();
            final PlayerDto matchWinner = matchWinnersMap.get(match);
            final int numberOfSets = extractNumberOfNonNullSets(match);
            final boolean statsWinnerWon = matchWinner.equals(statsWinner);
            rows.add(new HeadToHeadChartRow(match.getDate(), numberOfSets, statsWinnerWon));
        }
        return rows;
    }

    private int extractNumberOfNonNullSets(final MatchDto match) {
        return (int) match.getSets().stream().filter(SetDto::isNonEmpty).count();
    }

    @Override
    public String toString() {
        if (winner != null && looser != null) {
            return "HEAD-2-HEAD: " + winner.getPlayer() + " v. " + looser.getPlayer();
        } else {
            return "HEAD-2-HEAD: EMPTY";
        }
    }
}
