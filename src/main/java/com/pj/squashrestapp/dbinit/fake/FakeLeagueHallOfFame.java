package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.Trophy;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@UtilityClass
public class FakeLeagueHallOfFame {

  public List<TrophyForLeague> create(final SeasonScoreboardDto seasonScoreboardDto, final List<Player> allPlayers) {
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

    final List<TrophyForLeague> trophiesForLeagueForSeason = new ArrayList<>();

    final Player pretendersCupWinner = findMatchingPlayer(playersOrderedByPretendersCupPoints.get(0), allPlayers);
    trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, pretendersCupWinner, Trophy.PRETENDERS_CUP));

    final Player leagueFirstPlace = findMatchingPlayer(playersOrderedByCountedPoints.get(0), allPlayers);
    final Player leagueSecondPlace = findMatchingPlayer(playersOrderedByCountedPoints.get(1), allPlayers);
    final Player leagueThirdPlace = findMatchingPlayer(playersOrderedByCountedPoints.get(2), allPlayers);
    trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, leagueFirstPlace, Trophy.LEAGUE_1ST));
    trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, leagueSecondPlace, Trophy.LEAGUE_2ND));
    trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, leagueThirdPlace, Trophy.LEAGUE_3RD));

    final Player cupFirstPlace = findMatchingPlayer(threeRandomPlayersFromTopFive.get(0), allPlayers);
    final Player cupSecondPlace = findMatchingPlayer(threeRandomPlayersFromTopFive.get(1), allPlayers);
    final Player cupThirdPlace = findMatchingPlayer(threeRandomPlayersFromTopFive.get(2), allPlayers);
    trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, cupFirstPlace, Trophy.CUP_1ST));
    trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, cupSecondPlace, Trophy.CUP_2ND));
    trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, cupThirdPlace, Trophy.CUP_3RD));


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

    final Player superCup = findMatchingPlayer(superCupWinner, allPlayers);
    trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, superCup, Trophy.SUPER_CUP));

    return trophiesForLeagueForSeason;
  }

  private Player findMatchingPlayer(final PlayerDto playerDto, final List<Player> allPlayers) {
    return allPlayers
            .stream()
            .filter(player -> player.getUuid().equals(playerDto.getUuid()))
            .findFirst()
            .orElseThrow();
  }

}

















