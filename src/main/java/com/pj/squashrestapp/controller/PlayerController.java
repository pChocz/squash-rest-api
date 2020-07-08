package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.config.security.token.TokenConstants;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

  @Autowired
  private PlayerService playerService;


  @DeleteMapping(value = "/cleanBlacklistedTokens")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  void cleanBlacklistedTokens() {
    final int removedTokens = playerService.removeBlacklistedTokensFromDb();
    log.info("Removed {} expired tokens.", removedTokens);
  }


  @PostMapping(value = "/logout")
  @ResponseBody
  void logout(@RequestHeader(name = TokenConstants.HEADER_STRING) final String bearerToken) {
    playerService.blacklistToken(bearerToken);
    log.info("Logged-out");
  }


  @PostMapping(value = "/sign-up")
  @ResponseBody
  PlayerDetailedDto signUpPlayer(
          @RequestParam("username") final String username,
          @RequestParam("email") final String email,
          @RequestParam("password") final String password) {

    final boolean isValid = playerService.isValidSignupData(username, email, password);
    if (isValid) {
      final Player newPlayer = playerService.registerNewUser(username, email, password);
      return new PlayerDetailedDto(newPlayer);
    }
    return null;
  }


  @GetMapping(value = "/{playerId}")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  PlayerDetailedDto onePlayerInfoById(
          @PathVariable final Long playerId) {

    final Player player = playerRepository.fetchForAuthorizationById(playerId).get();
    final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
    return userBasicInfo;
  }


  @GetMapping
  @ResponseBody
  @PreAuthorize("isAdmin()")
  List<PlayerDetailedDto> allPlayersInfo() {

    final List<Player> players = playerRepository.fetchForAuthorizationAll();
    final List<PlayerDetailedDto> usersBasicInfo = players
            .stream()
            .map(PlayerDetailedDto::new)
            .collect(Collectors.toList());
    return usersBasicInfo;
  }


  @GetMapping(value = "/league/{leagueId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  List<PlayerDetailedDto> byLeagueId(
          @PathVariable("leagueId") final Long leagueId) {

    final List<Player> players = playerRepository.fetchForAuthorizationForLeague(leagueId);
    final List<PlayerDetailedDto> usersBasicInfo = players
            .stream()
            .map(PlayerDetailedDto::new)
            .collect(Collectors.toList());
    return usersBasicInfo;
  }


  @PutMapping(value = "/{playerId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  PlayerDetailedDto assignLeagueRole(
          @PathVariable("playerId") final Long playerId,
          @RequestParam("leagueId") final Long leagueId,
          @RequestParam("leagueRole") final LeagueRole leagueRole) {

    final Player player = playerRepository.fetchForAuthorizationById(playerId).get();
    final League league = leagueRepository.findById(leagueId).get();
    final RoleForLeague roleForLeague = roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);
    player.addRole(roleForLeague);

    playerRepository.save(player);

    return new PlayerDetailedDto(player);
  }

}
