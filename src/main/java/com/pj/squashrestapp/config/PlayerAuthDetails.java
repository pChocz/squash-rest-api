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
  private final Multimap<String, String> rolesForLeagues;

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

  private Multimap<String, String> extractRolesForLeagues(final List<PlayerAuthDto> authDtoList) {
    final Multimap<String, String> multimap = HashMultimap.create();
    for (final PlayerAuthDto authDto : authDtoList) {
      multimap.put(authDto.getLeagueName(), authDto.getRole().name());
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
