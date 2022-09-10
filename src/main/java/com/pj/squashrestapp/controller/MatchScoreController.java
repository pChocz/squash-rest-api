package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.MatchScore;
import com.pj.squashrestapp.service.MatchScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/match-score")
@RequiredArgsConstructor
public class MatchScoreController {

    private final MatchScoreService matchScoreService;

    @PostMapping(value = "/{matchUuid}")
    @PreAuthorize(
            """
            hasRoleForMatch(#matchUuid, 'OWNER')
            or
            hasRoleForMatch(#matchUuid, 'MODERATOR')
            or
            (isPlayerOfRoundForMatch(#matchUuid) and !isMatchFinished(#matchUuid))
            """)
    MatchDetailedDto appendNew(
            @PathVariable final UUID matchUuid,
            @RequestBody final MatchScore matchScore) {
        final MatchDetailedDto matchWithScores = matchScoreService.appendNewScore(matchUuid, matchScore);
        return matchWithScores;
    }

    @DeleteMapping(value = "/last/{matchUuid}")
    @PreAuthorize(
            """
            hasRoleForMatch(#matchUuid, 'OWNER')
            or
            hasRoleForMatch(#matchUuid, 'MODERATOR')
            or
            (isPlayerOfRoundForMatch(#matchUuid) and !isMatchFinished(#matchUuid))
            """)
    MatchDetailedDto revertLast(@PathVariable final UUID matchUuid) {
        final MatchDetailedDto matchWithScores = matchScoreService.revertLastScore(matchUuid);
        return matchWithScores;
    }

    @DeleteMapping(value = "/all/{matchUuid}")
    @PreAuthorize(
            """
            hasRoleForMatch(#matchUuid, 'OWNER')
            or
            hasRoleForMatch(#matchUuid, 'MODERATOR')
            or
            (isPlayerOfRoundForMatch(#matchUuid) and !isMatchFinished(#matchUuid))
            """)
    MatchDetailedDto clearAll(@PathVariable final UUID matchUuid) {
        final MatchDetailedDto matchWithScores = matchScoreService.clearAll(matchUuid);
        return matchWithScores;
    }

    @GetMapping(value = "/{matchUuid}")
    @PreAuthorize(
            """
            hasRoleForMatch(#matchUuid, 'OWNER')
            or
            hasRoleForMatch(#matchUuid, 'MODERATOR')
            or
            isPlayerOfRoundForMatch(#matchUuid)
            """)
    MatchDetailedDto getMatchWithScores(@PathVariable final UUID matchUuid) {
        final MatchDetailedDto matchWithScores = matchScoreService.getMatchWithScores(matchUuid);
        return matchWithScores;
    }

}
