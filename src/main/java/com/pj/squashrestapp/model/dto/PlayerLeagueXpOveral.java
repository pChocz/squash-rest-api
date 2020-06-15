package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import lombok.Getter;

import java.util.List;

@Getter
public class PlayerLeagueXpOveral {

  PlayerDto player;
  int totalPoints;
  int countedPoints;
  int attendices;
  int average;

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
