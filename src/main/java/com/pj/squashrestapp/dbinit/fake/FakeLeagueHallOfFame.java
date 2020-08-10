package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@UtilityClass
public class FakeLeagueHallOfFame {

  public HallOfFameSeason create(final SeasonScoreboardDto seasonScoreboardDto) {
    final int seasonNumber = seasonScoreboardDto.getSeason().getSeasonNumber();

    final List<PlayerDto> playersOrderedByCountedPoints = seasonScoreboardDto
            .getSeasonScoreboardRows()
            .stream()
            .sorted(Comparator.comparingInt(SeasonScoreboardRowDto::getCountedPoints).reversed())
            .map(SeasonScoreboardRowDto::getPlayer)
            .collect(Collectors.toList());

    final List<PlayerDto> playersOrderedByPretendersCupPoints = seasonScoreboardDto
            .getSeasonScoreboardRows()
            .stream()
            .sorted(Comparator.comparingInt(SeasonScoreboardRowDto::getCountedPointsPretenders).reversed())
            .map(SeasonScoreboardRowDto::getPlayer)
            .collect(Collectors.toList());

    final List<PlayerDto> threeRandomPlayersFromTopFive = FakeUtil.pickThreeRandomPlayersFromTopFive(playersOrderedByCountedPoints);

    final HallOfFameSeason hallOfFameSeason = new HallOfFameSeason(seasonNumber);

    hallOfFameSeason.setPretendersCupWinner(playersOrderedByPretendersCupPoints.get(0).getUsername());

    hallOfFameSeason.setLeague1stPlace(playersOrderedByCountedPoints.get(0).getUsername());
    hallOfFameSeason.setLeague2ndPlace(playersOrderedByCountedPoints.get(1).getUsername());
    hallOfFameSeason.setLeague3rdPlace(playersOrderedByCountedPoints.get(2).getUsername());

    hallOfFameSeason.setCup1stPlace(threeRandomPlayersFromTopFive.get(0).getUsername());
    hallOfFameSeason.setCup2ndPlace(threeRandomPlayersFromTopFive.get(1).getUsername());
    hallOfFameSeason.setCup3rdPlace(threeRandomPlayersFromTopFive.get(2).getUsername());

    final PlayerDto superCupWinner;
    if (playersOrderedByCountedPoints.get(0).equals(threeRandomPlayersFromTopFive.get(0))) {
      superCupWinner = FakeUtil.randomBetweenTwoIntegers(0, 1) == 0
              ? threeRandomPlayersFromTopFive.get(0)
              : threeRandomPlayersFromTopFive.get(1);
    } else {
      superCupWinner = FakeUtil.randomBetweenTwoIntegers(0, 1) == 0
              ? playersOrderedByCountedPoints.get(0)
              : threeRandomPlayersFromTopFive.get(0);
    }

    hallOfFameSeason.setSuperCupWinner(superCupWinner.getUsername());
    return hallOfFameSeason;
  }

}
