package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.matchresultsdistribution.LeagueMatchResultDistribution;
import com.pj.squashrestapp.service.MatchResultsDistributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/match-results-distribution")
@RequiredArgsConstructor
public class MatchResultDistributionController {

    private final MatchResultsDistributionService matchResultsDistributionService;

    @GetMapping(value = "/{leagueUuid}")
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'PLAYER')")
    LeagueMatchResultDistribution histogramBatis(@PathVariable final UUID leagueUuid,
                                                 @RequestParam(required = false) final int[] seasonNumbers) {
        final LeagueMatchResultDistribution matchResultsDistribution = matchResultsDistributionService.createDistribution(leagueUuid, seasonNumbers);
        return matchResultsDistribution;
    }
}
