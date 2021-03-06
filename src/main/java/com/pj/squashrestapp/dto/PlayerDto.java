package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.Player;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDto {

  @EqualsAndHashCode.Include private final UUID uuid;
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
