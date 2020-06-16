package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserBasicInfoDto {

  @EqualsAndHashCode.Include
  final Long id;
  final String username;
  final String email;
  final List<AuthorityType> authorities;
  final List<LeagueRoleDto> leagueRoles;

  public UserBasicInfoDto(final Player player) {
    this.id = player.getId();
    this.username = player.getUsername();
    this.email = player.getEmail();

    this.authorities = new ArrayList<>();
    for (final Authority authority : player.getAuthorities()) {
      this.authorities.add(authority.getType());
    }

    this.leagueRoles = new ArrayList<>();
    for (final RoleForLeague role : player.getRoles()) {
      final LeagueRoleDto leagueRole = new LeagueRoleDto(role);
      this.leagueRoles.add(leagueRole);
    }
  }

}
