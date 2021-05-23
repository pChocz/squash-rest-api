package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.LeagueRule;
import com.pj.squashrestapp.service.LeagueRulesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/league-rules")
@RequiredArgsConstructor
public class LeagueRulesController {

  private final LeagueRulesService leagueRulesService;


  @PutMapping(value = "/{leagueUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void addNewLeagueRule(@PathVariable final UUID leagueUuid,
                        @RequestParam final String rule) {
    leagueRulesService.addNewLeagueRule(leagueUuid, rule);
  }


  @DeleteMapping(value = "/{ruleUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeagueOfRule(#ruleUuid, 'MODERATOR')")
  void deleteRuleForLeague(@PathVariable final UUID ruleUuid) {
    leagueRulesService.deleteRule(ruleUuid);
  }


  @GetMapping(value = "/{leagueUuid}")
  @ResponseBody
  List<LeagueRule> extractRulesForLeague(@PathVariable final UUID leagueUuid) {
    final List<LeagueRule> leagueRules = leagueRulesService.extractRulesForLeague(leagueUuid);
    return leagueRules;
  }

}
