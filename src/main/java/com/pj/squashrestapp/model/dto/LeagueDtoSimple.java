package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.League;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 *
 */
@Slf4j
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LeagueDtoSimple {

  @EqualsAndHashCode.Include
  private final UUID uuid;
  private final String name;

  public LeagueDtoSimple(final League league) {
    this.uuid = league.getUuid();
    this.name = league.getName();
  }

  @Override
  public String toString() {
    return name;
  }

}
