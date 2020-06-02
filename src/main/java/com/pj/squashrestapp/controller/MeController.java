package com.pj.squashrestapp.controller;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@RestController
@RequestMapping("/me")
public class MeController {

  @Autowired
  private PlayerRepository playerRepository;

  @RequestMapping(
          value = "/info",
          method = GET)
  @ResponseBody
  public UserBasicInfo aboutMe() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.findByUsername(auth.getName());
    final UserBasicInfo userBasicInfo = new UserBasicInfo(player, auth);
    return userBasicInfo;
  }


  @RequestMapping(
          value = "/getAllRoles",
          method = GET)
  @ResponseBody
  List<LeagueRole> getRoles() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final List<LeagueRole> rolesForUserByLeague = playerRepository.findAllRolesForUsername(auth.getName());
    return rolesForUserByLeague;
  }


  @RequestMapping(
          value = "/getAuthorityTypes",
          method = GET)
  @ResponseBody
  Map<String, Collection<LeagueRole>> getAuthorityTypes() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.findByUsername(auth.getName());
    final UserBasicInfo userBasicInfo = new UserBasicInfo(player, auth);
    return userBasicInfo.getRoles();
  }



  @Getter
  @Setter
  static class UserBasicInfo {
    final Long id;
    final String username;
    final String email;
    final List<AuthorityType> authorities;
    final Map<String, Collection<LeagueRole>> roles;
    final boolean authenticated;

    UserBasicInfo(final Player player, final Authentication auth) {
      this.id = player.getId();
      this.username = player.getUsername();
      this.email = player.getEmail();

      this.authorities = new ArrayList<>();
      for (final Authority authority : player.getAuthorities()) {
        this.authorities.add(authority.getType());
      }

      final Multimap<String, LeagueRole> map = LinkedHashMultimap.create();
      for (final RoleForLeague role : player.getRoles()) {
        map.put(role.getLeague().getName(), role.getLeagueRole());
      }
      this.roles = map.asMap();

      this.authenticated = auth.isAuthenticated();
    }

  }

}











