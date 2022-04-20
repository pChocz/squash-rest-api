package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.service.LeagueLogoService;
import com.pj.squashrestapp.service.RoundService;
import com.pj.squashrestapp.service.SeasonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** */
@Slf4j
@RestController
@RequestMapping("/league-logos")
@RequiredArgsConstructor
public class LeagueLogoController {

    private final LeagueLogoService leagueLogoService;
    private final RoundService roundService;
    private final SeasonService seasonService;

    @GetMapping(value = "/season/{seasonUuid}")
    String getLeagueLogoBySeasonUuid(@PathVariable final UUID seasonUuid) {
        final UUID leagueUuid = seasonService.extractLeagueUuid(seasonUuid);
        final byte[] leagueLogoBytes = leagueLogoService.extractLeagueLogo(leagueUuid);
        return Base64Utils.encodeToString(leagueLogoBytes);
    }

    @GetMapping(value = "/round/{roundUuid}")
    String getLeagueLogoByRoundUuid(@PathVariable final UUID roundUuid) {
        final UUID leagueUuid = roundService.extractLeagueUuid(roundUuid);
        final byte[] leagueLogoBytes = leagueLogoService.extractLeagueLogo(leagueUuid);
        return Base64Utils.encodeToString(leagueLogoBytes);
    }

    @GetMapping(value = "/{leagueUuid}")
    String getLeagueLogo(@PathVariable final UUID leagueUuid) {
        final byte[] leagueLogoBytes = leagueLogoService.extractLeagueLogo(leagueUuid);
        return Base64Utils.encodeToString(leagueLogoBytes);
    }
}
