package com.pj.squashrestapp.config;

import com.pj.squashrestapp.model.Player;
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
public class PlayerDetails implements UserDetails {

  @Getter(AccessLevel.PRIVATE)
  private final Player player;

  private final String password;
  private final String username;

  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;

  private final Collection<? extends GrantedAuthority> authorities;

  public PlayerDetails(final Player player) {
    this.player = player;
    this.password = player.getPassword();
    this.username = player.getUsername();
    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
    this.enabled = true;
    this.authorities = extractAuthorities();
  }

  private Collection<? extends GrantedAuthority> extractAuthorities() {
    final List<SimpleGrantedAuthority> collect = player
            .getAuthorities()
            .stream()
            .map(type -> new SimpleGrantedAuthority(type.getType().name()))
            .collect(Collectors.toList());
    return collect;
  }

}
