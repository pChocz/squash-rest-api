package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
@AllArgsConstructor
public class PerPlayerMatchStats {

  private final long matches;
  private final PlayerDto player;

}
