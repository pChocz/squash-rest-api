package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.playerroundsstats.PlayerAllRoundsStats;
import com.pj.squashrestapp.service.PlayersRoundsStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** */
@Slf4j
@RestController
@RequestMapping("/rounds-stats")
@RequiredArgsConstructor
public class RoundsStatsController {

    private final PlayersRoundsStatsService playersRoundsStatsService;

    @GetMapping(value = "/{leagueUuid}/{playerUuid}")
    PlayerAllRoundsStats getRoundsStatsForPlayer(
            @PathVariable final UUID leagueUuid, @PathVariable final UUID playerUuid) {
        final PlayerAllRoundsStats roundsStatsForPlayer =
                playersRoundsStatsService.buildRoundsStatsForPlayer(leagueUuid, playerUuid);
        return roundsStatsForPlayer;
    }
}
