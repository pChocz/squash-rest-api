package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface LeagueRulesRepository extends JpaRepository<LeagueRule, Long> {

  List<LeagueRule> findAllByLeagueOrderByOrderValueAsc(League league);

  Optional<LeagueRule> findByUuid(UUID uuid);

  @Query("""
          SELECT l.uuid FROM LeagueRule lr
            INNER JOIN lr.league l
              WHERE lr.uuid = :ruleUuid
          """)
  UUID retrieveLeagueUuidOfRule(UUID ruleUuid);

}
