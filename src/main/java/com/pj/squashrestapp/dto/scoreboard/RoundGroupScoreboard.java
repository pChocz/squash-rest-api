package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** */
@Slf4j
@Getter
@NoArgsConstructor
public class RoundGroupScoreboard {

    private int roundGroupNumber;
    private List<RoundGroupScoreboardRow> scoreboardRows;
    private Collection<MatchDetailedDto> matches;

    public RoundGroupScoreboard(final Collection<MatchDetailedDto> matches) {
        this.matches = matches;

        this.roundGroupNumber = matches.stream()
                .findFirst()
                .map(MatchDetailedDto::getRoundGroupNumber)
                .orElse(0);

        this.scoreboardRows = new ArrayList<>();

        for (final MatchDetailedDto match : matches) {
            final RoundGroupScoreboardRow scoreboardRowFirst = getScoreboardRow(match.getFirstPlayer());
            final RoundGroupScoreboardRow scoreboardRowSecond = getScoreboardRow(match.getSecondPlayer());

            if (match.getStatus() == MatchStatus.FINISHED) {
                scoreboardRowFirst.applyMatch(match);
                scoreboardRowSecond.applyMatch(match);
            }
        }

        Collections.sort(scoreboardRows);
    }

    private RoundGroupScoreboardRow getScoreboardRow(final PlayerDto player) {
        RoundGroupScoreboardRow scoreboardRowFirst = scoreboardRows.stream()
                .filter(e -> e.getPlayer().equals(player))
                .findFirst()
                .orElse(null);

        if (scoreboardRowFirst == null) {
            scoreboardRowFirst = new RoundGroupScoreboardRow(player);
            scoreboardRows.add(scoreboardRowFirst);
        }
        return scoreboardRowFirst;
    }
}
