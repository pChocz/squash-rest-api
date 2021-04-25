package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface LeagueRulesRepository extends JpaRepository<LeagueRule, Long> {

  List<LeagueRule> findAllByLeague(League league);

  Optional<LeagueRule> findByUuid(UUID uuid);

}
