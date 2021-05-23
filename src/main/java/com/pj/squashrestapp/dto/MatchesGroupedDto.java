package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import lombok.Getter;

/** */
@Getter
public class MatchesGroupedDto {

  private final long count;
  private final Player opponent;
  private final League league;

  public MatchesGroupedDto(final Object object, final Player currentPlayer) {
    final Object[] array = (Object[]) object;

    final Player firstPlayer = (Player) array[1];
    final Player secondPlayer = (Player) array[2];

    this.opponent = firstPlayer.equals(currentPlayer) ? secondPlayer : firstPlayer;

    this.count = (long) array[0];
    this.league = (League) array[3];
  }
}
