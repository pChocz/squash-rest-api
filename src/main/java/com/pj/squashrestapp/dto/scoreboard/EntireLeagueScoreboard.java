package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.PlayerLeagueXpOveral;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/** */
@Slf4j
@Getter
@NoArgsConstructor
public class EntireLeagueScoreboard {

    private String leagueName;
    private int numberOfMatches;
    private List<EntireLeagueScoreboardRow> rows;

    public EntireLeagueScoreboard(final League league) {
        this.leagueName = league.getName();
        this.numberOfMatches = 0;
        this.rows = new ArrayList<>();
    }

    public EntireLeagueScoreboard(final League league, final List<PlayerLeagueXpOveral> playerLeagueXpOveralList) {
        final List<MatchDetailedDto> matches = MatchExtractorUtil.extractAllMatches(league);

        this.numberOfMatches = matches.size();
        this.leagueName = league.getName();

        final RoundGroupScoreboard roundGroupScoreboard = new RoundGroupScoreboard(matches);

        this.rows = new ArrayList<>();
        for (final PlayerLeagueXpOveral playerLeagueXpOveral : playerLeagueXpOveralList) {
            final PlayerDto player = playerLeagueXpOveral.getPlayer();

            final RoundGroupScoreboardRow scoreboardRowForPlayer = roundGroupScoreboard.getScoreboardRows().stream()
                    .filter(scoreboardRow -> scoreboardRow.getPlayer().equals(player))
                    .findFirst()
                    .orElse(null);

            final EntireLeagueScoreboardRow entireLeagueScoreboardRow =
                    new EntireLeagueScoreboardRow(playerLeagueXpOveral, scoreboardRowForPlayer);
            this.rows.add(entireLeagueScoreboardRow);
        }
    }
}
