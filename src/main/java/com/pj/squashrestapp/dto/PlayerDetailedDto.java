package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDetailedDto {

  @EqualsAndHashCode.Include final UUID uuid;
  final Long id;
  final String username;
  final String emoji;
  final String email;
  final String locale;
  final List<AuthorityType> authorities;
  final List<LeagueRoleDto> leagueRoles;
  final boolean nonLocked;
  final boolean enabled;
  final boolean wantsEmails;
  final Long successfulLoginAttempts;

  @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
  final LocalDateTime registrationDateTime;

  @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
  final LocalDateTime lastLoggedInDateTime;

  public PlayerDetailedDto(final Player player) {
    this.id = player.getId();
    this.uuid = player.getUuid();
    this.username = player.getUsername();
    this.emoji = player.getEmoji();
    this.email = player.getEmail();
    this.locale = player.getLocale();
    this.nonLocked = player.isNonLocked();
    this.enabled = player.isEnabled();
    this.wantsEmails = player.getWantsEmails();
    this.successfulLoginAttempts = player.getSuccessfulLoginAttempts();
    this.registrationDateTime = player.getRegistrationDateTime();
    this.lastLoggedInDateTime = player.getLastLoggedInDateTime();

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
