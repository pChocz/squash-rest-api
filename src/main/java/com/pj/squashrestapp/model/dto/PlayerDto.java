package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 *
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDto {

  @EqualsAndHashCode.Include
  private final UUID uuid;
  private final String username;

  public PlayerDto(final Player player) {
    this.uuid = player.getUuid();
    this.username = player.getUsername();
  }

  @Override
  public String toString() {
    return username;
  }

}
