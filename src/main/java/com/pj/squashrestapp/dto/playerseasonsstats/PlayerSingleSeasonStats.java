package com.pj.squashrestapp.dto.playerseasonsstats;

import com.pj.squashrestapp.dto.SeasonDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSingleSeasonStats {

    private int placeInSeason;
    private SeasonDto season;
    private SeasonScoreboardRowDto seasonScoreboardRow;

}
