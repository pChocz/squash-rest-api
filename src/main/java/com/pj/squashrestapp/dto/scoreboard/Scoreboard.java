package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Getter
@NoArgsConstructor
public class Scoreboard {

    private int numberOfMatches;
    private List<PlayersStatsScoreboardRow> scoreboardRows;

    public Scoreboard(final Collection<MatchDto> matches) {
        this.numberOfMatches = matches.size();
        this.scoreboardRows = new ArrayList<>();

        for (final MatchDto match : getSortedMatches(matches)) {
            final PlayersStatsScoreboardRow scoreboardRowFirst = getScoreboardRowOrBuildNew(match.getFirstPlayer());
            scoreboardRowFirst.applyMatch(match);

            final PlayersStatsScoreboardRow scoreboardRowSecond = getScoreboardRowOrBuildNew(match.getSecondPlayer());
            scoreboardRowSecond.applyMatch(match);
        }
        Collections.sort(scoreboardRows);
    }

    private List<MatchDto> getSortedMatches(final Collection<MatchDto> matches) {
        return matches.stream()
                .sorted(Comparator.comparing(MatchDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    private PlayersStatsScoreboardRow getScoreboardRowOrBuildNew(final PlayerDto player) {
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

    public void makeItSinglePlayerScoreboard(final UUID playerUuid) {
        final Iterator<PlayersStatsScoreboardRow> iterator = this.scoreboardRows.iterator();
        while (iterator.hasNext()) {
            final ScoreboardRow scoreboardRow = iterator.next();
            final UUID currentPlayerUuid = scoreboardRow.getPlayer().getUuid();
            if (!currentPlayerUuid.equals(playerUuid)) {
                iterator.remove();
            }
        }
    }

    public void removeSinglePlayer(final UUID playerUuid) {
        final Iterator<PlayersStatsScoreboardRow> iterator = this.scoreboardRows.iterator();
        while (iterator.hasNext()) {
            final ScoreboardRow scoreboardRow = iterator.next();
            final UUID currentPlayerUuid = scoreboardRow.getPlayer().getUuid();
            if (currentPlayerUuid.equals(playerUuid)) {
                iterator.remove();
                break;
            }
        }
    }

    public PlayersStatsScoreboardRow getRowForPlayer(final PlayerDto player) {
        return this.scoreboardRows.stream()
                .filter(row -> row.getPlayer().equals(player))
                .findFirst()
                .orElse(new PlayersStatsScoreboardRow(player));
    }

    public void reverse() {
        for (final PlayersStatsScoreboardRow row : this.scoreboardRows) {
            final int matchesWon = row.getMatchesWon();
            final int matchesLost = row.getMatchesLost();
            final int setsWon = row.getSetsWon();
            final int setsLost = row.getSetsLost();
            final int pointsWon = row.getPointsWon();
            final int pointsLost = row.getPointsLost();

            row.setMatchesWon(matchesLost);
            row.setMatchesLost(matchesWon);
            row.setSetsWon(setsLost);
            row.setSetsLost(setsWon);
            row.setPointsWon(pointsLost);
            row.setPointsLost(pointsWon);
        }
        Collections.sort(this.scoreboardRows);
        Collections.reverse(this.scoreboardRows);
    }

    @Override
    public String toString() {
        return "matches: "
                + numberOfMatches
                + " | "
                + scoreboardRows.stream()
                        .map(PlayersStatsScoreboardRow::toString)
                        .collect(Collectors.joining(", "));
    }
}
