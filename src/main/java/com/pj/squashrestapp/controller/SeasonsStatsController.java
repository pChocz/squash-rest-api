package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.playerseasonsstats.PlayerAllSeasonsStats;
import com.pj.squashrestapp.service.PlayersSeasonsStatsService;
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
@RequestMapping("/seasons-stats")
@RequiredArgsConstructor
public class SeasonsStatsController {

    private final PlayersSeasonsStatsService playersSeasonsStatsService;

    @GetMapping(value = "/{leagueUuid}/{playerUuid}")
    PlayerAllSeasonsStats getSeasonsStatsForPlayer(
            @PathVariable final UUID leagueUuid, @PathVariable final UUID playerUuid) {
        final PlayerAllSeasonsStats seasonsStatsForPlayer =
                playersSeasonsStatsService.buildSeasonsStatsForPlayer(leagueUuid, playerUuid);
        return seasonsStatsForPlayer;
    }
}
