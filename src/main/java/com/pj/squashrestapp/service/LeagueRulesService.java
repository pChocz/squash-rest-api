package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRule;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.LeagueRulesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueRulesService {

  private final LeagueRulesRepository leagueRulesRepository;
  private final LeagueRepository leagueRepository;


  @Transactional
  public void addNewLeagueRule(final UUID leagueUuid, final String rule) {
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();

    final LeagueRule leagueRule = new LeagueRule(rule);
    league.addRuleForLeague(leagueRule);

    leagueRepository.save(league);
  }

  public void deleteRule(final UUID ruleUuid) {
    final LeagueRule leagueRule = leagueRulesRepository.findByUuid(ruleUuid).orElseThrow();
    leagueRulesRepository.delete(leagueRule);
  }

  public List<LeagueRule> extractRulesForLeague(final UUID leagueUuid) {
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final List<LeagueRule> rules = leagueRulesRepository.findAllByLeagueOrderByOrderValueAsc(league);
    return rules;
  }

}
