package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.RoundGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleForLeagueRepository extends JpaRepository<RoleForLeague, Long> {

  RoleForLeague findByLeagueAndLeagueRole(League league, LeagueRole leagueRole);

}
