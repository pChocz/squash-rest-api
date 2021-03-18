package com.pj.squashrestapp.dbinit.jsondto.util;

import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueTrophy;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.model.dto.Trophy;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class JsonImportUtil {

  public Season constructSeason(final JsonSeason jsonSeason) {
    final Season season = new Season();
    season.setNumber(jsonSeason.getNumber());
    season.setStartDate(jsonSeason.getStartDate());
    season.setUuid(jsonSeason.getUuid());
    season.setXpPointsType(jsonSeason.getXpPointsType());
    return season;
  }

  public Round constructRound(final JsonRound jsonRound) {
    final Round round = new Round();
    round.setNumber(jsonRound.getNumber());
    round.setDate(jsonRound.getDate());
    round.setUuid(jsonRound.getUuid());
    round.setFinished(true);
    return round;
  }

  public RoundGroup constructRoundGroup(final JsonRoundGroup jsonRoundGroup) {
    final RoundGroup roundGroup = new RoundGroup();
    roundGroup.setNumber(jsonRoundGroup.getNumber());
    return roundGroup;
  }

  public Match constructMatch(final JsonMatch jsonMatch, final List<Player> players) {
    final Player firstPlayer = getCorrespondingPlayer(players, jsonMatch.getFirstPlayer());
    final Player secondPlayer = getCorrespondingPlayer(players, jsonMatch.getSecondPlayer());
    final Match match = new Match(firstPlayer, secondPlayer);
    return match;
  }

  private Player getCorrespondingPlayer(final List<Player> players, final UUID playerUuid) {
    return players
            .stream()
            .filter(player -> player
                    .getUuid()
                    .equals(playerUuid))
            .findFirst()
            .orElse(null);
  }

  public SetResult constructSetResult(final int setNumber, final JsonSetResult jsonSetResult) {
    final SetResult setResult = new SetResult();
    setResult.setNumber(setNumber);
    setResult.setFirstPlayerScore(jsonSetResult.getFirstPlayerResult());
    setResult.setSecondPlayerScore(jsonSetResult.getSecondPlayerResult());
    return setResult;
  }

  public SetResult constructEmptySetResult(final int setNumber) {
    final SetResult setResult = new SetResult();
    setResult.setNumber(setNumber);
    setResult.setFirstPlayerScore(null);
    setResult.setSecondPlayerScore(null);
    return setResult;
  }

  public void setSplitForRound(final Round round) {
    final List<Integer> splitList = new ArrayList<>();

    for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
      final Set<Player> uniquePlayers = new HashSet<>();
      for (final Match match : roundGroup.getMatches()) {
        uniquePlayers.add(match.getFirstPlayer());
        uniquePlayers.add(match.getSecondPlayer());
      }
      final int numberOfPlayers = uniquePlayers.size();
      splitList.add(numberOfPlayers);
    }

    round.setSplit(GeneralUtil.integerListToString(splitList));
  }

  public TrophyForLeague constructLeagueTrophy(final JsonLeagueTrophy jsonLeagueTrophy,
                                               final List<Player> players) {

    final Player player = players
            .stream()
            .filter(p -> p.getUuid().equals(jsonLeagueTrophy.getPlayerUuid()))
            .findFirst()
            .orElseThrow();

    final TrophyForLeague trophyForLeague = new TrophyForLeague();
    trophyForLeague.setSeasonNumber(jsonLeagueTrophy.getSeasonNumber());
    trophyForLeague.setPlayer(player);
    trophyForLeague.setTrophy(jsonLeagueTrophy.getTrophy());

    return trophyForLeague;



//    final List<TrophyForLeague> trophiesForLeagueForSeason = new ArrayList<>();

//    if (jsonLeagueTrophy.getLeague1stPlace() != null) {
//      final Player player = getMatchingPlayer(jsonLeagueTrophy.getLeague1stPlace(), players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.LEAGUE_1ST));
//    }
//    if (jsonLeagueTrophy.getLeague2ndPlace() != null) {
//      final Player player = getMatchingPlayer(jsonLeagueTrophy.getLeague2ndPlace(), players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.LEAGUE_2ND));
//    }
//    if (jsonLeagueTrophy.getLeague3rdPlace() != null) {
//      final Player player = getMatchingPlayer(jsonLeagueTrophy.getLeague3rdPlace(), players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.LEAGUE_3RD));
//    }
//
//
//    if (jsonLeagueTrophy.getCup1stPlace() != null) {
//      final Player player = getMatchingPlayer(jsonLeagueTrophy.getCup1stPlace(), players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.CUP_1ST));
//    }
//    if (jsonLeagueTrophy.getCup2ndPlace() != null) {
//      final Player player = getMatchingPlayer(jsonLeagueTrophy.getCup2ndPlace(), players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.CUP_2ND));
//    }
//    if (jsonLeagueTrophy.getCup3rdPlace() != null) {
//      final Player player = getMatchingPlayer(jsonLeagueTrophy.getCup3rdPlace(), players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.CUP_3RD));
//    }
//
//
//    if (jsonLeagueTrophy.getPretendersCupWinner() != null) {
//      final Player player = getMatchingPlayer(jsonLeagueTrophy.getPretendersCupWinner(), players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.PRETENDERS_CUP));
//    }
//    if (jsonLeagueTrophy.getSuperCupWinner() != null) {
//      final Player player = getMatchingPlayer(jsonLeagueTrophy.getSuperCupWinner(), players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.SUPER_CUP));
//    }
//
//
//    for (final UUID playerUuid : jsonLeagueTrophy.getAllRoundsAttendees()) {
//      final Player player = getMatchingPlayer(playerUuid, players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.ALL_ROUNDS_ATTENDEE));
//    }
//    for (final UUID playerUuid : jsonLeagueTrophy.getCoviders()) {
//      final Player player = getMatchingPlayer(playerUuid, players);
//      trophiesForLeagueForSeason.add(new TrophyForLeague(seasonNumber, player, Trophy.COVID));
//    }
//
//    return trophiesForLeagueForSeason;
  }

  private static Player getMatchingPlayer(final UUID playerUuid, final List<Player> players) {
    return players
            .stream()
            .filter(player -> player.getUuid().equals(playerUuid))
            .findFirst()
            .orElseThrow();
  }

  public BonusPoint constructBonusPoints(final JsonBonusPoint jsonBonusPoint, final List<Player> players) {
    final BonusPoint bonusPoint = new BonusPoint();
    bonusPoint.setUuid(jsonBonusPoint.getUuid());
    bonusPoint.setDate(jsonBonusPoint.getDate());
    bonusPoint.setWinner(getCorrespondingPlayer(players, jsonBonusPoint.getWinner()));
    bonusPoint.setLooser(getCorrespondingPlayer(players, jsonBonusPoint.getLooser()));
    bonusPoint.setPoints(jsonBonusPoint.getPoints());
    return bonusPoint;
  }

  public League constructLeague(final JsonLeague jsonLeague) {
    final League league = new League();
    league.setName(jsonLeague.getName());
    league.setLeagueLogo(constructLeagueLogo(jsonLeague));
    league.setUuid(jsonLeague.getUuid());
    return league;
  }

  private LeagueLogo constructLeagueLogo(final JsonLeague jsonLeague) {
    final LeagueLogo leagueLogo = new LeagueLogo();
    final byte[] logoBytes = Base64.getDecoder().decode(jsonLeague.getLogoBase64());
    leagueLogo.setPicture(logoBytes);
    return leagueLogo;
  }

}
