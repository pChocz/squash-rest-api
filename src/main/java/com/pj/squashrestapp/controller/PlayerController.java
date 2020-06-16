package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.dto.UserBasicInfoDto;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/players")
public class PlayerController {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private RoleForLeagueRepository roleForLeagueRepository;


  @GetMapping(value = "/{playerId}")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  UserBasicInfoDto onePlayerInfoById(@PathVariable final Long playerId) {
    final Player player = playerRepository.fetchForAuthorizationById(playerId).get();
    final UserBasicInfoDto userBasicInfo = new UserBasicInfoDto(player);
    return userBasicInfo;
  }


  @GetMapping
  @ResponseBody
  @PreAuthorize("isAdmin()")
  List<UserBasicInfoDto> allPlayersInfo() {
    final List<Player> players = playerRepository.fetchForAuthorizationAll();
    final List<UserBasicInfoDto> usersBasicInfo = players
            .stream()
            .map(UserBasicInfoDto::new)
            .collect(Collectors.toList());
    return usersBasicInfo;
  }


  @GetMapping(value = "/league/{leagueId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  List<UserBasicInfoDto> byLeagueId(@PathVariable("leagueId") final Long leagueId) {
    final List<Player> players = playerRepository.fetchForAuthorizationForLeague(leagueId);
    final List<UserBasicInfoDto> usersBasicInfo = players
            .stream()
            .map(UserBasicInfoDto::new)
            .collect(Collectors.toList());
    return usersBasicInfo;
  }


  @PutMapping(value = "/{playerId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  UserBasicInfoDto assignLeagueRole(
          @PathVariable("playerId") final Long playerId,
          @RequestParam("leagueId") final Long leagueId,
          @RequestParam("leagueRole") final LeagueRole leagueRole) {

    final Player player = playerRepository.fetchForAuthorizationById(playerId).get();
    final League league = leagueRepository.findById(leagueId).get();
    final RoleForLeague roleForLeague = roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);
    player.addRole(roleForLeague);

    playerRepository.save(player);

    return new UserBasicInfoDto(player);
  }

}
