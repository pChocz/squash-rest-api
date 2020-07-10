package com.pj.squashrestapp.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class UserDetailsImpl implements UserDetails {

  private final String username;
  private final String password;
  private final Set<GrantedAuthority> authorities;
  private final Multimap<Long, String> rolesForLeagues;
  private final boolean enabled;
  private final LocalDateTime lastPasswordChangeDateTime;

  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;

  public UserDetailsImpl(final Player player) {
    this.username = player.getUsername();
    this.password = player.getPassword();
    this.authorities = extractAuthorities(player.getAuthorities());
    this.rolesForLeagues = extractRolesForLeagues(player.getRoles());
    this.enabled = player.isEnabled();
    this.lastPasswordChangeDateTime = player.getLastPasswordChangeDateTime();

    // todo: check what to do with it later
    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
  }

  private Set<GrantedAuthority> extractAuthorities(final Set<Authority> authorities) {
    return authorities
            .stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getType().name()))
            .collect(Collectors.toSet());
  }

  private Multimap<Long, String> extractRolesForLeagues(final Set<RoleForLeague> roles) {
    final Multimap<Long, String> multimap = HashMultimap.create();
    for (final RoleForLeague role : roles) {
      multimap.put(role.getLeague().getId(), role.getLeagueRole().name());
    }
    return multimap;
  }

  public boolean isAdmin() {
    return authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  public boolean hasRoleForLeague(final Long leagueId, final String role) {
    final Collection<String> rolesForLeague = rolesForLeagues.get(leagueId);
    return (rolesForLeague == null)
            ? false
            : rolesForLeague.contains(role);
  }

}
