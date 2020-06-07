package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 *
 */
@Value
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDto {

  @EqualsAndHashCode.Include
  Long id;
  String username;

  public PlayerDto(final Player player) {
    this.id = player.getId();
    this.username = player.getUsername();
  }

  @Override
  public String toString() {
    return username;
  }

}
