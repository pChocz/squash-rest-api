package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.RoleForLeague;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface RoleForLeagueRepository extends JpaRepository<RoleForLeague, Long> {


  @EntityGraph(attributePaths = {
          "players"
  })
  RoleForLeague findByLeagueAndLeagueRole(League league, LeagueRole leagueRole);


  List<RoleForLeague> findByLeague(League league);

}
