package com.pj.squashrestapp.dto.encounters;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.RoundDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PlayersEncounter {

    private RoundDto round;
    private MatchSimpleDto directMatch;
    private Integer xpPointsDifference;
    private WinningType winningType;
    private Integer winningDifference;
    private RoundGroupScoreboardRow firstPlayerRow;
    private RoundGroupScoreboardRow secondPlayerRow;
    private PlayerDto winner;

    public PlayersEncounter(final RoundScoreboard roundScoreboard, final PlayerDto firstPlayer, final PlayerDto secondPlayer) {
        this.round = new RoundDto(roundScoreboard);
        int firstPlayerGroup = 0;
        int secondPlayerGroup = 0;
        for (final RoundGroupScoreboard roundGroupScoreboard : roundScoreboard.getRoundGroupScoreboards()) {
            for (final RoundGroupScoreboardRow row : roundGroupScoreboard.getScoreboardRows()) {
                if (row.getPlayer().equals(firstPlayer)) {
                    this.firstPlayerRow = row;
                    firstPlayerGroup = roundGroupScoreboard.getRoundGroupNumber();
                }
                if (row.getPlayer().equals(secondPlayer)) {
                    this.secondPlayerRow = row;
                    secondPlayerGroup = roundGroupScoreboard.getRoundGroupNumber();
                }
            }
        }
        this.xpPointsDifference = firstPlayerRow.getXpEarned() - secondPlayerRow.getXpEarned();
        if (firstPlayerGroup == secondPlayerGroup) {
            // the same group
            this.directMatch = findDirectMatch(roundScoreboard, firstPlayer, secondPlayer);
            final int matchBalanceDiff = firstPlayerRow.getMatchesBalance() - secondPlayerRow.getMatchesBalance();
            final int gamesBalanceDiff = firstPlayerRow.getSetsBalance() - secondPlayerRow.getSetsBalance();
            final int ralliesBalanceDiff = firstPlayerRow.getPointsBalance() - secondPlayerRow.getPointsBalance();
            if (matchBalanceDiff != 0) {
                this.winningType = WinningType.BY_MATCHES;
                this.winningDifference = matchBalanceDiff;

            } else if (gamesBalanceDiff != 0) {
                this.winningType = WinningType.BY_GAMES;
                this.winningDifference = gamesBalanceDiff;

            } else {
                this.winningType = WinningType.BY_RALLIES;
                this.winningDifference = ralliesBalanceDiff;
            }

        } else {
            // different group
            this.directMatch = null;
            this.winningType = WinningType.BY_GROUPS;
            this.winningDifference = secondPlayerGroup - firstPlayerGroup;
        }

        if (winningDifference > 0) {
            this.winner = firstPlayer;
        } else if (winningDifference < 0) {
            this.winner = secondPlayer;
        }
    }

    private MatchSimpleDto findDirectMatch(final RoundScoreboard roundScoreboard, final PlayerDto firstPlayer, final PlayerDto secondPlayer) {
        return roundScoreboard
                .getRoundGroupScoreboards()
                .stream()
                .flatMap(v -> v.getMatches().stream())
                .filter(m -> Set.of(firstPlayer, secondPlayer).equals(Set.of(m.getFirstPlayer(), m.getSecondPlayer())))
                .findFirst()
                .map(MatchSimpleDto::new)
                .orElseThrow();
    }
}
