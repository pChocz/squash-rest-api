package com.pj.squashrestapp.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class PlayerAuthDetails implements UserDetails {

  @Getter(AccessLevel.PRIVATE)
  private final Player player;

  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final Multimap<String, String> rolesForLeagues;

  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;

  public PlayerAuthDetails(final Player player) {
    this.player = player;
    this.username = player.getUsername();
    this.password = player.getPassword();
    this.authorities = extractAuthorities();
    this.rolesForLeagues = extractRolesForLeagues();

    // todo: check what to do with it later
    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
    this.enabled = true;
  }

  private Collection<? extends GrantedAuthority> extractAuthorities() {
    return player
            .getAuthorities()
            .stream()
            .map(auth -> new SimpleGrantedAuthority(auth.getType().name()))
            .collect(Collectors.toList());
  }

  private Multimap<String, String> extractRolesForLeagues() {
    final Multimap<String, String> multimap = HashMultimap.create();
    for (final RoleForLeague role : player.getRoles()) {
      multimap.put(role.getLeague().getName(), role.getLeagueRole().name());
    }
    return multimap;
  }

  public boolean isPlayerOfLeague(final String leagueName) {
    final Collection<String> rolesForLeague = rolesForLeagues.get(leagueName);
    return (rolesForLeague == null)
            ? false
            : rolesForLeague.contains("PLAYER");
  }

}
