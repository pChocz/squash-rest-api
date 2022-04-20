package com.pj.squashrestapp.dto.playerseasonsstats;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.leaguestats.SeasonTrophies;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor
public class PlayerAllSeasonsStats {

    private PlayerDto player;
    private List<PlayerSingleSeasonStats> playerSingleSeasonStats;
    private List<SeasonTrophies> seasonTrophies;

    public PlayerAllSeasonsStats(final PlayerDto player, final List<SeasonTrophies> seasonTrophies) {
        this.player = player;
        this.seasonTrophies = seasonTrophies;
        this.playerSingleSeasonStats = new ArrayList<>();
    }

    public void addSingleSeasonStats(final PlayerSingleSeasonStats playerSingleSeasonStats) {
        this.playerSingleSeasonStats.add(playerSingleSeasonStats);
    }
}
