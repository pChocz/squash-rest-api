package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.enums.LeagueRole;
import com.pj.squashrestapp.model.RoleForLeague;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** */
public interface RoleForLeagueRepository extends JpaRepository<RoleForLeague, Long> {

    @EntityGraph(attributePaths = {"players", "league"})
    RoleForLeague findByLeagueAndLeagueRole(League league, LeagueRole leagueRole);

    List<RoleForLeague> findByLeague(League league);
}
