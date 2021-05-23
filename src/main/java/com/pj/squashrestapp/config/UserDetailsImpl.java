package com.pj.squashrestapp.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** */
@Getter
public class UserDetailsImpl implements UserDetails {

  private final String username;
  private final String password;
  private final Set<GrantedAuthority> authorities;
  private final Multimap<UUID, LeagueRole> rolesForLeagues;
  private final boolean enabled;
  private final UUID uuid;
  private final UUID passwordSessionUuid;

  // not used
  private final boolean accountNonExpired = true;
  private final boolean accountNonLocked = true;
  private final boolean credentialsNonExpired = true;

  public UserDetailsImpl(final Player player) {
    this.username = player.getUsername();
    this.password = player.getPassword();
    this.authorities = extractAuthorities(player.getAuthorities());
    this.rolesForLeagues = extractRolesForLeagues(player.getRoles());
    this.enabled = player.isEnabled();
    this.uuid = player.getUuid();
    this.passwordSessionUuid = player.getPasswordSessionUuid();
  }

  private Set<GrantedAuthority> extractAuthorities(final Set<Authority> authorities) {
    return authorities.stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getType().name()))
        .collect(Collectors.toSet());
  }

  private Multimap<UUID, LeagueRole> extractRolesForLeagues(final Set<RoleForLeague> roles) {
    final Multimap<UUID, LeagueRole> multimap = HashMultimap.create();
    for (final RoleForLeague role : roles) {
      multimap.put(role.getLeague().getUuid(), role.getLeagueRole());
    }
    return multimap;
  }

  public boolean isAdmin() {
    return authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  public boolean hasRoleForLeague(final UUID leagueUuid, final LeagueRole role) {
    final Collection<LeagueRole> rolesForLeague = rolesForLeagues.get(leagueUuid);
    return (rolesForLeague == null) ? false : rolesForLeague.contains(role);
  }
}
