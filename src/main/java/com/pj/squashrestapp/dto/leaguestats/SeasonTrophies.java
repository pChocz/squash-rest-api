package com.pj.squashrestapp.dto.leaguestats;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.model.TrophyForLeague;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
public class SeasonTrophies {

  private final int seasonNumber;
  private final List<TrophyDto> trophies;

  public SeasonTrophies(final int seasonNumber, final List<TrophyForLeague> trophies) {
    this.seasonNumber = seasonNumber;
    this.trophies = new ArrayList<>();
    for (final TrophyForLeague trophyForLeague : trophies) {
      this.trophies.add(
              new TrophyDto(
                      trophyForLeague.getTrophy(),
                      new PlayerDto(trophyForLeague.getPlayer())
              )
      );
    }
  }

}
