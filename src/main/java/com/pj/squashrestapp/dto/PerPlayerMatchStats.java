package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.dto.PlayerDto;
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
