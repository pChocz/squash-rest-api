package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PutMapping(value = "/{matchUuid}")
    @PreAuthorize(
            """
            hasRoleForMatch(#matchUuid, 'OWNER')
            or
            hasRoleForMatch(#matchUuid, 'MODERATOR')
            or
            (isPlayerOfRoundForMatch(#matchUuid) and !isMatchFinished(#matchUuid))
            """)
    MatchSimpleDto updateMatchSingleScore(
            @PathVariable final UUID matchUuid,
            @RequestParam final int setNumber,
            @RequestParam final String player,
            @RequestParam final Integer newScore) {
        final MatchSimpleDto editedMatch = matchService.modifySingleScore(matchUuid, setNumber, player, newScore);
        return editedMatch;
    }

    @GetMapping(value = "/for-league-for-players/{leagueUuid}/{playersUuids}")
    MatchesSimplePaginated getMatchesPageable(
            @PageableDefault(
                            sort = {"id", "number"},
                            direction = Sort.Direction.DESC)
                    final Pageable pageable,
            @PathVariable final UUID leagueUuid,
            @PathVariable final UUID[] playersUuids,
            @RequestParam(required = false) final UUID seasonUuid,
            @RequestParam(required = false) final Integer groupNumber) {

        final MatchesSimplePaginated matchesPaginated =
                matchService.getRoundMatchesPaginated(pageable, leagueUuid, playersUuids, seasonUuid, groupNumber);
        return matchesPaginated;
    }

    @GetMapping(value = "/for-league-for-players-additional/{leagueUuid}/{playersUuids}")
    MatchesSimplePaginated getAdditionalMatchesPageable(
            @PageableDefault(
                            sort = {"id", "date"},
                            direction = Sort.Direction.DESC)
                    final Pageable pageable,
            @PathVariable final UUID leagueUuid,
            @PathVariable final UUID[] playersUuids,
            @RequestParam(required = false) final UUID seasonUuid) {

        final MatchesSimplePaginated matchesPaginated =
                matchService.getAdditionalMatchesPaginated(pageable, leagueUuid, playersUuids, seasonUuid);
        return matchesPaginated;
    }
}
