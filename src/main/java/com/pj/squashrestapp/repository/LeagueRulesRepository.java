package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRule;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface LeagueRulesRepository extends JpaRepository<LeagueRule, Long> {

  List<LeagueRule> findAllByLeagueOrderByOrderValueAsc(League league);

  Optional<LeagueRule> findByUuid(UUID uuid);

}
