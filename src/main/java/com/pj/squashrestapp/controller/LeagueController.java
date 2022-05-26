package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.aspects.SecretMethod;
import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.PlayerForLeagueDto;
import com.pj.squashrestapp.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.dto.leaguestats.OveralStats;
import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetWinningType;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** */
@Slf4j
@RestController
@RequestMapping("/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;
    private final RedisCacheService redisCacheService;

    @SecretMethod
    @PostMapping
    UUID createLeague(
            @RequestParam final String leagueName,
            @RequestParam final String logoBase64,
            @RequestParam final int numberOfRounds,
            @RequestParam final int numberOfRoundsToBeDeducted,
            @RequestParam final MatchFormatType matchFormatType,
            @RequestParam final SetWinningType regularSetWinningType,
            @RequestParam final int regularSetWinningPoints,
            @RequestParam final SetWinningType tiebreakWinningType,
            @RequestParam final int tiebreakWinningPoints,
            @RequestParam(required = false) final String leagueWhen,
            @RequestParam(required = false) final String leagueWhere) {

        final UUID newLeagueUuid = leagueService.createNewLeague(
                leagueName,
                logoBase64,
                numberOfRounds,
                numberOfRoundsToBeDeducted,
                matchFormatType,
                regularSetWinningType,
                regularSetWinningPoints,
                tiebreakWinningType,
                tiebreakWinningPoints,
                leagueWhen,
                leagueWhere);

        return newLeagueUuid;
    }

    @DeleteMapping(value = "/{leagueUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'OWNER')")
    void deleteLeague(@PathVariable final UUID leagueUuid) {
        leagueService.removeLeague(leagueUuid);
        redisCacheService.clearAll();
    }

    @SecretMethod
    @PutMapping(value = "/owner/{leagueUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'OWNER')")
    void updateLeagueAsOwner(
            @PathVariable final UUID leagueUuid,
            @RequestBody final Optional<String> logoBase64,
            @RequestParam final Optional<String> leagueName,
            @RequestParam final Optional<String> location,
            @RequestParam final Optional<String> time) {
        leagueService.updateLeagueAsOwner(leagueUuid, logoBase64, leagueName, location, time);
        redisCacheService.clearAll();
    }

    @SecretMethod
    @PutMapping(value = "/moderator/{leagueUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
    void updateLeagueAsModerator(
            @PathVariable final UUID leagueUuid,
            @RequestParam final Optional<String> location,
            @RequestParam final Optional<String> time) {
        leagueService.updateLeagueAsModerator(leagueUuid, location, time);
        redisCacheService.clearAll();
    }

    @GetMapping(value = "/general-info/{leagueUuid}")
    LeagueDto getLeagueGeneralInfo(@PathVariable final UUID leagueUuid) {
        final LeagueDto leagueGeneralInfo = leagueService.buildGeneralInfoForLeague(leagueUuid);
        return leagueGeneralInfo;
    }

    @GetMapping(value = "/general-info")
    List<LeagueDto> getAllLeaguesGeneralInfo() {
        final List<LeagueDto> allLeaguesGeneralInfo = leagueService.buildGeneralInfoForAllLeagues();
        return allLeaguesGeneralInfo;
    }

    @GetMapping(value = "/all-logos")
    Map<UUID, byte[]> getAllLeaguesLogosMap() {
        final Map<UUID, byte[]> allLeaguesLogos = leagueService.extractAllLogos();
        return allLeaguesLogos;
    }

    @GetMapping(value = "/players/{leagueUuid}")
    List<PlayerDto> getPlayersGeneralInfoByLeague(@PathVariable final UUID leagueUuid) {
        final List<PlayerDto> playersGeneralInfo = leagueService.extractLeaguePlayersGeneral(leagueUuid);
        return playersGeneralInfo;
    }

    @GetMapping(value = "/players-for-league-moderator/{leagueUuid}")
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR') or hasRoleForLeague(#leagueUuid, 'OWNER')")
    List<PlayerForLeagueDto> getPlayersForLeagueModeratorByLeagueUuid(@PathVariable final UUID leagueUuid) {
        final List<PlayerForLeagueDto> playersForLeague = leagueService.extractLeaguePlayersForLeague(leagueUuid);
        return playersForLeague;
    }

    @GetMapping(value = "/stats/{leagueUuid}")
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'PLAYER')")
    LeagueStatsWrapper getLeagueStatistics(@PathVariable final UUID leagueUuid) {
        final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueUuid(leagueUuid);
        return leagueStatsWrapper;
    }

    @GetMapping(value = "/overal-stats/{leagueUuid}")
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'PLAYER')")
    OveralStats getLeagueOveralStats(@PathVariable final UUID leagueUuid) {
        final OveralStats leagueOveralStats = leagueService.buildOveralStatsForLeagueUuid(leagueUuid);
        return leagueOveralStats;
    }

    @GetMapping(value = "/name-taken/{leagueName}")
    boolean getIsLeagueNameTaken(@PathVariable final String leagueName) {
        final boolean isLeagueNameTaken = leagueService.checkLeagueNameTaken(leagueName);
        return isLeagueNameTaken;
    }
}
