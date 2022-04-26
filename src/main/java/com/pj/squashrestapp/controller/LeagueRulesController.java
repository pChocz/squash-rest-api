package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.LeagueRuleDto;
import com.pj.squashrestapp.model.LeagueRuleType;
import com.pj.squashrestapp.service.LeagueRulesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** */
@Slf4j
@RestController
@RequestMapping("/league-rules")
@RequiredArgsConstructor
public class LeagueRulesController {

    private final LeagueRulesService leagueRulesService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'OWNER')")
    void addNewLeagueRule(
            @RequestParam final UUID leagueUuid,
            @RequestParam final String rule,
            @RequestParam final LeagueRuleType type) {
        leagueRulesService.addNewLeagueRule(leagueUuid, rule, type);
    }

    @PutMapping(value = "/{ruleUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRoleForLeagueRule(#ruleUuid, 'OWNER')")
    void updateLeagueRule(
            @PathVariable final UUID ruleUuid,
            @RequestParam final Optional<String> rule,
            @RequestParam final Optional<LeagueRuleType> type,
            @RequestParam final Optional<Double> orderValue) {
        leagueRulesService.updateLeagueRule(ruleUuid, rule, type, orderValue);
    }

    @DeleteMapping(value = "/{ruleUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRoleForLeagueRule(#ruleUuid, 'OWNER')")
    void deleteRuleForLeague(@PathVariable final UUID ruleUuid) {
        leagueRulesService.deleteRule(ruleUuid);
    }

    @GetMapping(value = "/for-league/{leagueUuid}")
    @PreAuthorize("hasRoleForLeague(#leagueUuid, 'PLAYER')")
    List<LeagueRuleDto> getRulesForLeague(@PathVariable final UUID leagueUuid) {
        final List<LeagueRuleDto> leagueRules = leagueRulesService.extractRulesForLeague(leagueUuid);
        return leagueRules;
    }
}
