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
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping("/me")
public class OldUserController {

  @Autowired
  private PlayerRepository playerRepository;

  @RequestMapping
  public Object aboutMe() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    final ArrayList<? extends GrantedAuthority> grantedAuthorities = new ArrayList<>(auth.getAuthorities());
    if (grantedAuthorities.size() == 1
            && grantedAuthorities.get(0).getAuthority().equals("ROLE_ANONYMOUS")  ) {
      return "Welcome Anonymous User";
    }

    final Player player = playerRepository.findByUsername(auth.getName());
    return new UserBasicInfo(player, auth);
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











