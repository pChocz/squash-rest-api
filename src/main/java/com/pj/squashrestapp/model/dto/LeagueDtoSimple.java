package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.League;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Getter
public class LeagueDtoSimple {

  private final UUID uuid;
  private final String name;

  public LeagueDtoSimple(final League league) {
    this.uuid = league.getUuid();
    this.name = league.getName();
  }

}
