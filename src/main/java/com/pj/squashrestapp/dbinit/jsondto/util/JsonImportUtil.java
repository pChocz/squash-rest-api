package com.pj.squashrestapp.dbinit.jsondto.util;

import com.pj.squashrestapp.dbinit.jsondto.JsonAdditionalMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueTrophy;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditonalSetResult;
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
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;

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
    return players.stream()
        .filter(player -> player.getUuid().equals(playerUuid))
        .findFirst()
        .orElse(null);
  }

  public AdditionalMatch constructAdditionalMatch(
      final JsonAdditionalMatch jsonMatch, final List<Player> players) {
    final Player firstPlayer = getCorrespondingPlayer(players, jsonMatch.getFirstPlayer());
    final Player secondPlayer = getCorrespondingPlayer(players, jsonMatch.getSecondPlayer());
    final AdditionalMatch match = new AdditionalMatch(firstPlayer, secondPlayer);
    match.setDate(jsonMatch.getDate());
    match.setType(jsonMatch.getType());
    match.setSeasonNumber(jsonMatch.getSeasonNumber());
    return match;
  }

  public SetResult constructSetResult(final int setNumber, final JsonSetResult jsonSetResult) {
    final SetResult setResult = new SetResult();
    setResult.setNumber(setNumber);
    setResult.setFirstPlayerScore(jsonSetResult.getFirstPlayerResult());
    setResult.setSecondPlayerScore(jsonSetResult.getSecondPlayerResult());
    return setResult;
  }

  public AdditonalSetResult constructAdditionalSetResult(
      final int setNumber, final JsonSetResult jsonSetResult) {
    final AdditonalSetResult setResult = new AdditonalSetResult();
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

  public AdditonalSetResult constructEmptyAdditionalSetResult(final int setNumber) {
    final AdditonalSetResult setResult = new AdditonalSetResult();
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

  public TrophyForLeague constructLeagueTrophy(
      final JsonLeagueTrophy jsonLeagueTrophy, final List<Player> players) {

    final Player player =
        players.stream()
            .filter(p -> p.getUuid().equals(jsonLeagueTrophy.getPlayerUuid()))
            .findFirst()
            .orElseThrow();

    final TrophyForLeague trophyForLeague = new TrophyForLeague();
    trophyForLeague.setSeasonNumber(jsonLeagueTrophy.getSeasonNumber());
    trophyForLeague.setPlayer(player);
    trophyForLeague.setTrophy(jsonLeagueTrophy.getTrophy());

    return trophyForLeague;
  }

  public BonusPoint constructBonusPoints(
      final JsonBonusPoint jsonBonusPoint, final List<Player> players) {
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
    league.setTime(jsonLeague.getTime());
    league.setLocation(jsonLeague.getLocation());
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
