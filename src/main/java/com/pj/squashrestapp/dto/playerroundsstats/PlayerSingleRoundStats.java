package com.pj.squashrestapp.dto.playerroundsstats;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.RoundDto;
import com.pj.squashrestapp.dto.SetDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboardRow;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
public class PlayerSingleRoundStats {

  final int seasonNumber;
  final int roundGroupNumber;
  final String split;
  final RoundDto round;
  final RoundGroupScoreboardRow row;
  final List<RoundOpponent> roundOpponents;

  public PlayerSingleRoundStats(
      final Player player, final Round round, final List<Integer> xpPoints) {
    this.seasonNumber = round.getSeason().getNumber();
    this.split = round.getSplit();
    this.round = new RoundDto(round);

    final List<MatchDetailedDto> matches =
        round.getRoundGroups().stream().findFirst().orElseThrow().getMatches().stream()
            .map(MatchDetailedDto::new)
            .collect(Collectors.toList());

    final RoundGroupScoreboard roundGroupScoreboard = new RoundGroupScoreboard(matches);
    int place = 1;
    for (final RoundGroupScoreboardRow row : roundGroupScoreboard.getScoreboardRows()) {
      row.setPlaceInGroup(place++);
    }
    this.roundGroupNumber = roundGroupScoreboard.getRoundGroupNumber();

    final RoundGroupScoreboardRow correctRow =
        roundGroupScoreboard.getScoreboardRows().stream()
            .filter(row -> row.getPlayer().getUuid().equals(player.getUuid()))
            .findFirst()
            .orElseThrow();

    correctRow.setPlaceInRound(
        calculatePlaceInRound(
            roundGroupScoreboard.getRoundGroupNumber(), correctRow.getPlaceInGroup()));
    correctRow.setXpEarned(xpPoints.get(correctRow.getPlaceInRound() - 1));

    this.row = correctRow;

    final PlayerDto currentPlayer = new PlayerDto(player);
    this.roundOpponents = new ArrayList<>();
    for (final RoundGroupScoreboardRow row : roundGroupScoreboard.getScoreboardRows()) {
      if (row.getPlayer().equals(currentPlayer)) {
        this.roundOpponents.add(new RoundOpponent(currentPlayer, true, row.getPlaceInGroup()));

      } else {
        final PlayerDto opponent = row.getPlayer();
        final MatchDetailedDto match =
            matches.stream().filter(predicate(currentPlayer, opponent)).findFirst().orElseThrow();
        final boolean hasWon = hasCurrentPlayerWonMatch(currentPlayer, match);
        this.roundOpponents.add(new RoundOpponent(opponent, hasWon, row.getPlaceInGroup()));
      }
    }
  }

  private int calculatePlaceInRound(final int roundGroupNumber, final int placeInGroup) {
    final int[] splitAsArray = getSplitAsArray();
    return placeInGroup + Arrays.stream(splitAsArray, 0, roundGroupNumber - 1).sum();
  }

  private Predicate<? super MatchDetailedDto> predicate(
      final PlayerDto currentPlayer, final PlayerDto opponent) {
    return (Predicate<MatchDetailedDto>)
        matchDetailedDto ->
            Set.of(currentPlayer, opponent)
                .equals(
                    Set.of(matchDetailedDto.getFirstPlayer(), matchDetailedDto.getSecondPlayer()));
  }

  private boolean hasCurrentPlayerWonMatch(
      final PlayerDto currentPlayer, final MatchDetailedDto match) {
    int firstPlayerWonSets = 0;
    int secondPlayerWonSets = 0;
    for (final SetDto set : match.getSets()) {
      if (!set.isEmpty()) {
        if (set.getFirstPlayerScoreNullSafe() > set.getSecondPlayerScoreNullSafe()) {
          firstPlayerWonSets++;
        } else if (set.getFirstPlayerScoreNullSafe() < set.getSecondPlayerScoreNullSafe()) {
          secondPlayerWonSets++;
        }
      }
    }

    final PlayerDto winner =
        firstPlayerWonSets > secondPlayerWonSets ? match.getFirstPlayer() : match.getSecondPlayer();

    return currentPlayer.equals(winner);
  }

  private int[] getSplitAsArray() {
    final int[] splitAsArray =
        Arrays.stream(this.split.split("\\|"))
            .map(String::trim)
            .mapToInt(Integer::valueOf)
            .toArray();
    return splitAsArray;
  }

  @Override
  public String toString() {
    return "S: " + seasonNumber + " | R: " + round.getRoundNumber() + " | RG: " + roundGroupNumber;
  }
}
