package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.LeagueRuleDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRule;
import com.pj.squashrestapp.model.enums.LeagueRuleType;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.LeagueRulesRepository;
import com.pj.squashrestapp.util.GsonUtil;
import com.pj.squashrestapp.util.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueRulesService {

    private final LeagueRulesRepository leagueRulesRepository;
    private final LeagueRepository leagueRepository;

    @Transactional
    public void addNewLeagueRule(final UUID leagueUuid, final String rule, final LeagueRuleType type) {
        final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
        final LeagueRule leagueRule = new LeagueRule(rule, type);
        league.addRuleForLeague(leagueRule);
        leagueRepository.save(league);
        log.info("Created: {}", JacksonUtil.objectToJson(new LeagueRuleDto(leagueRule)));
    }

    public void deleteRule(final UUID ruleUuid) {
        final LeagueRule leagueRule = leagueRulesRepository.findByUuid(ruleUuid).orElseThrow();
        leagueRulesRepository.delete(leagueRule);
        log.info("Deleted: {}", JacksonUtil.objectToJson(new LeagueRuleDto(leagueRule)));
    }

    public List<LeagueRuleDto> extractRulesForLeague(final UUID leagueUuid) {
        final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
        return leagueRulesRepository
                .findAllByLeagueOrderByOrderValueAscIdAsc(league)
                .stream()
                .map(LeagueRuleDto::new)
                .collect(Collectors.toList());
    }

    public void updateLeagueRule(
            final UUID ruleUuid,
            final Optional<String> rule,
            final Optional<LeagueRuleType> type,
            final Optional<Double> orderValue) {
        final LeagueRule leagueRule = leagueRulesRepository.findByUuid(ruleUuid).orElseThrow();
        final String leagueRuleInitialJson = JacksonUtil.objectToJson(new LeagueRuleDto(leagueRule));
        rule.ifPresent(leagueRule::setRule);
        type.ifPresent(leagueRule::setType);
        if (orderValue.isPresent()) {
            leagueRule.setOrderValue(orderValue.get());
        } else {
            leagueRule.setOrderValue(null);
        }
        leagueRulesRepository.save(leagueRule);
        log.info("League rule updated [{}]", leagueRule.getUuid());
        log.info("BEFORE: {}", leagueRuleInitialJson);
        log.info("AFTER: {}", JacksonUtil.objectToJson(new LeagueRuleDto(leagueRule)));
    }
}
