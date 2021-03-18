package com.pj.squashrestapp.dto.leaguestats;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.Trophy;
import com.pj.squashrestapp.model.TrophyForLeague;
import lombok.Getter;

import java.util.List;

/**
 *
 */
@Getter
public class TrophyDto {

  private final Trophy trophy;
  private final PlayerDto player;

  public TrophyDto(final Trophy trophy, final PlayerDto player) {
    this.trophy = trophy;
    this.player = player;
  }

}
