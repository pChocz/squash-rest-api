package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "http://localhost:4200")
public class PlayerController {

  @Autowired
  private PlayerRepository playerRepository;

  @RequestMapping(
          value = "/byLeagueId",
          params = {"id", "role"},
          method = GET)
  @ResponseBody
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//  @Secured("ROLE_ADMIN")
  List<PlayerDetailedDto> byLeagueId(@RequestParam("id") final Long id,
                                     @RequestParam("role") final LeagueRole role) {
    final List<PlayerDetailedDto> players = playerRepository.findByLeague(id, role);
    return players;
  }

  @RequestMapping(
          value = "/getRoles",
          params = {"id", "leagueId"},
          method = GET)
  @ResponseBody
  List<LeagueRole> getRoles(@RequestParam("id") final Long id,
                            @RequestParam("leagueId") final Long leagueId) {
    final List<LeagueRole> rolesForUserByLeague = playerRepository.findRolesForUserByLeague(id, leagueId);
    return rolesForUserByLeague;
  }

  @RequestMapping(
          value = "/getAuthorityTypes",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<AuthorityType> getAuthorityTypes(@RequestParam("id") final Long id) {
    final List<AuthorityType> authorityTypes = playerRepository.findAuthoritiesForUser(id);
    return authorityTypes;
  }


}
