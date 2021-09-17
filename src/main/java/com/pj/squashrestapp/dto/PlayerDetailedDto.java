package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDetailedDto {

  @EqualsAndHashCode.Include final UUID uuid;
  final String username;
  final String emoji;
  final String email;
  final List<AuthorityType> authorities;
  final List<LeagueRoleDto> leagueRoles;

  public PlayerDetailedDto(final Player player) {
    this.uuid = player.getUuid();
    this.username = player.getUsername();
    this.emoji = player.getEmoji();
    this.email = player.getEmail();

    this.authorities = new ArrayList<>();
    for (final Authority authority : player.getAuthorities()) {
      this.authorities.add(authority.getType());
    }

    final List<RoleForLeague> rolesSorted =
        player.getRoles().stream()
            .sorted(Comparator.comparing(o -> o.getLeague().getDateOfCreation()))
            .collect(Collectors.toList());

    this.leagueRoles = new ArrayList<>();
    for (final RoleForLeague role : rolesSorted) {
      final LeagueRoleDto leagueRole = new LeagueRoleDto(role);
      this.leagueRoles.add(leagueRole);
    }
  }

  public boolean isPlayerForLeague(final String leagueName) {
    return this.leagueRoles.stream()
        .filter(leagueRoleDto -> leagueRoleDto.getLeagueRole().equals(LeagueRole.PLAYER))
        .anyMatch(leagueRoleDto -> leagueRoleDto.getLeagueName().equals(leagueName));
  }

  public boolean isPlayerForLeague(final UUID leagueUuid) {
    return this.leagueRoles.stream()
        .filter(leagueRoleDto -> leagueRoleDto.getLeagueRole().equals(LeagueRole.PLAYER))
        .anyMatch(leagueRoleDto -> leagueRoleDto.getLeagueUuid().equals(leagueUuid));
  }

  public boolean hasAnyRoleForLeague(final UUID leagueUuid) {
    return this.leagueRoles.stream()
        .anyMatch(leagueRoleDto -> leagueRoleDto.getLeagueUuid().equals(leagueUuid));
  }

  @Override
  public String toString() {
    return username + " | uuid: " + uuid;
  }

  public List<LeagueRole> getLeagueRolesForLeague(final UUID leagueUuid) {
    return this.leagueRoles.stream()
        .filter(leagueRoleDto -> leagueRoleDto.getLeagueUuid().equals(leagueUuid))
        .map(LeagueRoleDto::getLeagueRole)
        .collect(Collectors.toList());
  }

}
