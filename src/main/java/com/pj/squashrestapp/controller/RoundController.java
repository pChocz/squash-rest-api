package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.service.RedisCacheService;
import com.pj.squashrestapp.service.RoundService;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** */
@Slf4j
@RestController
@RequestMapping("/rounds")
@RequiredArgsConstructor
public class RoundController {

    private final RedisCacheService redisCacheService;
    private final RoundService roundService;

    @PostMapping
    @PreAuthorize("hasRoleForSeason(#seasonUuid, 'MODERATOR')")
    UUID createRound(
            @RequestBody @RequestParam final int roundNumber,
            @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_FORMAT) final LocalDate roundDate,
            @RequestParam final UUID seasonUuid,
            @RequestParam final List<UUID[]> playersUuids) {
        final Round round = roundService.createRound(roundNumber, roundDate, seasonUuid, playersUuids);
        redisCacheService.evictCacheForRoundMatches(round.getUuid());
        redisCacheService.evictCacheForRound(round.getUuid());
        return round.getUuid();
    }

    @PutMapping(value = "{roundUuid}")
    @PreAuthorize("hasRoleForRound(#roundUuid, 'MODERATOR')")
    void recreateRound(@PathVariable final UUID roundUuid, @RequestParam final List<UUID[]> playersUuids) {
        redisCacheService.evictCacheForPlayersHeadToHead(playersUuids);
        redisCacheService.evictCacheForRoundMatches(roundUuid);
        redisCacheService.evictCacheForRound(roundUuid);
        roundService.recreateRound(roundUuid, playersUuids);
    }

    @PutMapping(value = "{roundUuid}/{finishedState}")
    @PreAuthorize("hasRoleForRound(#roundUuid, 'MODERATOR')")
    void updateRoundFinishedState(@PathVariable final UUID roundUuid, @PathVariable final boolean finishedState) {
        redisCacheService.evictCacheForRound(roundUuid);
        roundService.updateRoundFinishedState(roundUuid, finishedState);
    }

    @GetMapping(value = "league-uuid/{roundUuid}")
    UUID getLeagueUuidFromRoundUuid(@PathVariable final UUID roundUuid) {
        return roundService.extractLeagueUuid(roundUuid);
    }

    @GetMapping(value = "/adjacent/{roundUuid}")
    Pair<Optional<UUID>, Optional<UUID>> getAdjacentRoundsUuids(@PathVariable final UUID roundUuid) {
        final Pair<Optional<UUID>, Optional<UUID>> adjacentRoundsUuids =
                roundService.extractAdjacentRoundsUuids(roundUuid);
        return adjacentRoundsUuids;
    }

    @DeleteMapping(value = "/{roundUuid}")
    @PreAuthorize("hasRoleForRound(#roundUuid, 'MODERATOR')")
    void deleteRound(@PathVariable final UUID roundUuid) {
        redisCacheService.evictCacheForRoundMatches(roundUuid);
        redisCacheService.evictCacheForRound(roundUuid);
        roundService.deleteRound(roundUuid);
    }
}
