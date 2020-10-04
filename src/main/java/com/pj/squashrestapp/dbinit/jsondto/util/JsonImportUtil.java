package com.pj.squashrestapp.dbinit.jsondto.util;

import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonHallOfFameSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class JsonImportUtil {

  public Season constructSeason(final JsonSeason jsonSeason) {
    final Season season = new Season();
    season.setNumber(jsonSeason.getNumber());
    season.setStartDate(jsonSeason.getStartDate());
    season.setUuid(jsonSeason.getUuid());
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

  private Player getCorrespondingPlayer(final List<Player> players, final String playerName) {
    return players
            .stream()
            .filter(player -> player
                    .getUsername()
                    .equals(playerName))
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

  public HallOfFameSeason constructHallOfFameSeason(final JsonHallOfFameSeason jsonHallOfFameSeason) {
    final HallOfFameSeason hallOfFameSeason = new HallOfFameSeason();
    hallOfFameSeason.setSeasonNumber(jsonHallOfFameSeason.getSeasonNumber());
    hallOfFameSeason.setLeague1stPlace(jsonHallOfFameSeason.getLeague1stPlace());
    hallOfFameSeason.setLeague2ndPlace(jsonHallOfFameSeason.getLeague2ndPlace());
    hallOfFameSeason.setLeague3rdPlace(jsonHallOfFameSeason.getLeague3rdPlace());
    hallOfFameSeason.setCup1stPlace(jsonHallOfFameSeason.getCup1stPlace());
    hallOfFameSeason.setCup2ndPlace(jsonHallOfFameSeason.getCup2ndPlace());
    hallOfFameSeason.setCup3rdPlace(jsonHallOfFameSeason.getCup3rdPlace());
    hallOfFameSeason.setSuperCupWinner(jsonHallOfFameSeason.getSuperCupWinner());
    hallOfFameSeason.setPretendersCupWinner(jsonHallOfFameSeason.getPretendersCupWinner());
    return hallOfFameSeason;
  }

  public BonusPoint constructBonusPoints(final JsonBonusPoint jsonBonusPoint, final List<Player> players) {
    final BonusPoint bonusPoint = new BonusPoint();
    bonusPoint.setPlayer(getCorrespondingPlayer(players, jsonBonusPoint.getPlayerName()));
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
