package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.setresultshistogram.ReadySetResultsHistogram;
import com.pj.squashrestapp.service.GameDistributionService;
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
@RequestMapping("/set-results-histogram")
@RequiredArgsConstructor
public class SetResultHistogramController {

    private final GameDistributionService gameDistributionService;

    @GetMapping(value = "/{leagueUuid}")
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'PLAYER')")
    ReadySetResultsHistogram extractLeagueSetsDistributionStats(@PathVariable final UUID leagueUuid,
                                                                @RequestParam(required = false) final int[] seasonNumbers,
                                                                @RequestParam(defaultValue = "false") final boolean includeAdditional) {
        ReadySetResultsHistogram histogram = gameDistributionService.create(leagueUuid, seasonNumbers, includeAdditional);
        return histogram;
    }
}
