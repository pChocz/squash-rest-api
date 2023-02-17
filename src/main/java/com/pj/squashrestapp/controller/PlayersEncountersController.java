package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.encounters.PlayersEncountersStats;
import com.pj.squashrestapp.service.PlayersEncountersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/players-encounters")
@RequiredArgsConstructor
public class PlayersEncountersController {

    private final PlayersEncountersService playersEncountersService;

    @GetMapping(value = "/{firstPlayerUuid}/{secondPlayerUuid}")
    PlayersEncountersStats getPlayersEncountersStatistics(
            @PathVariable final UUID firstPlayerUuid,
            @PathVariable final UUID secondPlayerUuid) {
        final PlayersEncountersStats stats = playersEncountersService.build(firstPlayerUuid, secondPlayerUuid);
        return stats;
    }
}
