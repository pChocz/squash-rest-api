package com.pj.squashrestapp.dto.leaguestats;

import com.pj.squashrestapp.dto.scoreboard.EntireLeagueScoreboard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/** */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeagueStatsWrapper {

    private String leagueName;
    private UUID leagueUuid;
    private List<PerSeasonStats> perSeasonStats;
    private EntireLeagueScoreboard scoreboard;

    @Override
    public String toString() {
        return "League Stats - " + leagueName;
    }
}
