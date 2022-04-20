package com.pj.squashrestapp.dto.playerroundsstats;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.RoundDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.SetDto;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboardRow;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/** */
@Slf4j
@Getter
@NoArgsConstructor
public class PlayerSingleRoundStats {

    private int seasonNumber;
    private int roundGroupNumber;
    private String roundGroupCharacter;
    private String split;
    private RoundDto round;
    private RoundGroupScoreboardRow row;
    private List<RoundOpponent> roundOpponents;

    private int playersInGroup;
    private int playersInRound;

    public PlayerSingleRoundStats(
            final Player player, final RoundDto roundDto, final RoundGroupScoreboard roundGroupScoreboard) {

        this.round = roundDto;
        this.seasonNumber = roundDto.getSeasonNumber();
        this.split = roundDto.getSplit();
        this.roundGroupNumber = roundGroupScoreboard.getRoundGroupNumber();
        this.roundGroupCharacter = String.valueOf((char) (roundGroupNumber + 'A' - 1));
        this.row = roundGroupScoreboard.getScoreboardRows().stream()
                .filter(row -> row.getPlayer().getUuid().equals(player.getUuid()))
                .findFirst()
                .orElseThrow();

        this.playersInGroup = roundGroupScoreboard.getScoreboardRows().size();
        this.playersInRound = GeneralUtil.splitToSum(split);

        final PlayerDto currentPlayer = new PlayerDto(player);
        this.roundOpponents = new ArrayList<>();
        for (final RoundGroupScoreboardRow roundGroupScoreboardRow : roundGroupScoreboard.getScoreboardRows()) {
            if (roundGroupScoreboardRow.getPlayer().equals(currentPlayer)) {
                this.roundOpponents.add(
                        new RoundOpponent(currentPlayer, true, roundGroupScoreboardRow.getPlaceInGroup()));

            } else {
                final PlayerDto opponent = roundGroupScoreboardRow.getPlayer();
                final MatchDetailedDto match = roundGroupScoreboard.getMatches().stream()
                        .filter(predicate(currentPlayer, opponent))
                        .findFirst()
                        .orElseThrow();
                final boolean hasWon = hasCurrentPlayerWonMatch(currentPlayer, match);
                this.roundOpponents.add(new RoundOpponent(opponent, hasWon, roundGroupScoreboardRow.getPlaceInGroup()));
            }
        }
    }

    private Predicate<? super MatchDetailedDto> predicate(final PlayerDto currentPlayer, final PlayerDto opponent) {
        return (Predicate<MatchDetailedDto>) matchDetailedDto -> Set.of(currentPlayer, opponent)
                .equals(Set.of(matchDetailedDto.getFirstPlayer(), matchDetailedDto.getSecondPlayer()));
    }

    private boolean hasCurrentPlayerWonMatch(final PlayerDto currentPlayer, final MatchDetailedDto match) {
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

    @Override
    public String toString() {
        return "S: " + seasonNumber + " | R: " + round.getRoundNumber() + " | RG: " + roundGroupNumber;
    }
}
