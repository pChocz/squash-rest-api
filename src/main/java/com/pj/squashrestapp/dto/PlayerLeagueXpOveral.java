package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import java.util.List;
import lombok.Getter;

/**
 *
 */
@Getter
public class PlayerLeagueXpOveral {

  private final PlayerDto player;
  private final int average;
  private int totalPoints;
  private int countedPoints;
  private int attendices;

  public PlayerLeagueXpOveral(final List<SeasonScoreboardRowDto> playerDtoCollection) {
    this.player = playerDtoCollection.get(0).getPlayer();
    this.totalPoints = 0;
    this.countedPoints = 0;
    this.attendices = 0;
    for (final SeasonScoreboardRowDto seasonScoreboardRowDto : playerDtoCollection) {
      this.totalPoints += seasonScoreboardRowDto.getTotalPoints();
      this.countedPoints += seasonScoreboardRowDto.getCountedPoints();
      this.attendices += seasonScoreboardRowDto.getAttendices();
    }
    this.average = totalPoints / attendices;
  }

}
