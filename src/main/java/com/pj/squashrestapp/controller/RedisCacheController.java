package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.dto.leaguestats.OveralStats;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.service.RedisCacheService;
import com.pj.squashrestapp.service.RoundService;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/redis-cache")
@RequiredArgsConstructor
@PreAuthorize("isAdmin()")
public class RedisCacheController {

    private final RedisCacheService redisCacheService;
    private final ScoreboardService scoreboardService;
    private final SeasonService seasonService;
    private final RoundService roundService;
    private final LeagueService leagueService;

    @GetMapping(value = "/recreate-leagues-small-scoreboards/{leaguesUuids}")
    @ResponseStatus(HttpStatus.OK)
    @SuppressWarnings("unused")
    public void recreateGivenLeaguesSmallScoreboardsCache(@PathVariable final List<UUID> leaguesUuids) {
        for (final UUID leagueUuid : leaguesUuids) {
            final List<UUID> roundUuids = roundService.extractAllRoundsUuidsForLeague(leagueUuid);
            final List<UUID> seasonUuids = seasonService.extractAllSeasonsUuidsForLeague(leagueUuid);
            for (final UUID roundUuid : roundUuids) {
                final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundUuid);
            }
            for (final UUID seasonUuid : seasonUuids) {
                final SeasonScoreboardDto seasonScoreboard = seasonService.overalScoreboard(seasonUuid);
            }
        }
    }

    @GetMapping(value = "/recreate-all-leagues-scoreboards")
    @ResponseStatus(HttpStatus.OK)
    @SuppressWarnings("unused")
    public void recreateAllLeaguesBigScoreboardsCache() {
        List<LeagueDto> allLeagues = leagueService.buildGeneralInfoForAllLeagues();
        for (final LeagueDto leagueDto : allLeagues) {
            final UUID leagueUuid = leagueDto.getLeagueUuid();
            final List<RoundScoreboard> roundScoreboards = scoreboardService.allRoundsScoreboards(leagueUuid);
            final List<SeasonScoreboardDto> seasonScoreboards = scoreboardService.allSeasonsScoreboards(leagueUuid);
            final OveralStats leagueOveralStats = leagueService.buildOveralStatsForLeagueUuid(leagueUuid);
            final LeagueStatsWrapper leagueStats = leagueService.buildStatsForLeagueUuid(leagueUuid);
        }
    }

    @GetMapping(value = "/all")
    Set<String> getAllRedisKeys() {
        return redisCacheService.getAllKeys();
    }

    @DeleteMapping(value = "/all")
    @ResponseStatus(HttpStatus.OK)
    public void clearWholeCache() {
        redisCacheService.clearAll();
    }

    @DeleteMapping(value = "/{cacheName}/{key}")
    @ResponseStatus(HttpStatus.OK)
    void deleteSingleRedisKey(@PathVariable final String cacheName, @PathVariable final String key) {
        redisCacheService.clearSingle(cacheName, key);
    }
}
