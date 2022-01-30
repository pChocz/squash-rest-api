package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerForLeagueDto {

  @EqualsAndHashCode.Include private UUID uuid;
  private String username;
  private String emoji;
  private Set<LeagueRole> leagueRoles;

  public PlayerForLeagueDto(final Player player, final UUID leagueUuid) {
    this.uuid = player.getUuid();
    this.username = player.getUsername();
    this.emoji = player.getEmoji();
    this.leagueRoles = new HashSet<>();
    this.leagueRoles = player
        .getRoles()
        .stream()
        .filter(role -> role.getLeague().getUuid().equals(leagueUuid))
        .map(RoleForLeague::getLeagueRole)
        .collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return username;
  }
}
