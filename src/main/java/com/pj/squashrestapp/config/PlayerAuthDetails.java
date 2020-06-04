package com.pj.squashrestapp.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.model.dto.PlayerAuthDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class PlayerAuthDetails implements UserDetails {

  private final String username;
  private final String password;
  private final Set<GrantedAuthority> authorities;
  private final Multimap<Long, String> rolesForLeagues;

  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;

  public PlayerAuthDetails(final List<PlayerAuthDto> authDtoList) {
    this.username = authDtoList.get(0).getUsername();
    this.password = authDtoList.get(0).getPassword();
    this.authorities = extractAuthorities(authDtoList);
    this.rolesForLeagues = extractRolesForLeagues(authDtoList);

    // todo: check what to do with it later
    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
    this.enabled = true;
  }

  private Set<GrantedAuthority> extractAuthorities(final List<PlayerAuthDto> authDtoList) {
    return authDtoList
            .stream()
            .map(auth -> new SimpleGrantedAuthority(auth.getAuthorityType().name()))
            .collect(Collectors.toSet());
  }

  private Multimap<Long, String> extractRolesForLeagues(final List<PlayerAuthDto> authDtoList) {
    final Multimap<Long, String> multimap = HashMultimap.create();
    for (final PlayerAuthDto authDto : authDtoList) {
      multimap.put(authDto.getLeagueId(), authDto.getRole().name());
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
