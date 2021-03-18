package com.pj.squashrestapp.dbinit.jsondto.util;

import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueTrophy;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayer;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.TrophyForLeague;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@UtilityClass
public class JsonExportUtil {

  public JsonLeague buildLeagueJson(final List<Player> allPlayers, final League league, final List<BonusPoint> bonusPoints) {
    final JsonLeague jsonLeague = new JsonLeague();
    jsonLeague.setUuid(league.getUuid());
    jsonLeague.setName(league.getName());
    jsonLeague.setLogoBase64(
            Base64
                    .getEncoder()
                    .encodeToString(
                            league
                                    .getLeagueLogo()
                                    .getPicture()));

    jsonLeague.setSeasons(buildSeasonsJson(league.getSeasonsOrdered(), bonusPoints));
    jsonLeague.setTrophies(buildTrophiesList(league.getTrophiesForLeague()));

    return jsonLeague;
  }

  private ArrayList<JsonLeagueTrophy> buildTrophiesList(final List<TrophyForLeague> trophiesForLeague) {
    final ArrayList<JsonLeagueTrophy> trophies = new ArrayList<>();

    for (final TrophyForLeague trophyForLeague : trophiesForLeague) {
      final JsonLeagueTrophy trophy = new JsonLeagueTrophy();
      trophy.setSeasonNumber(trophyForLeague.getSeasonNumber());
      trophy.setPlayerUuid(trophyForLeague.getPlayer().getUuid());
      trophy.setTrophy(trophyForLeague.getTrophy());
      trophies.add(trophy);
    }

    return trophies;
  }

  private ArrayList<JsonSeason> buildSeasonsJson(final List<Season> seasonsOrdered, final List<BonusPoint> bonusPoints) {
    final ArrayList<JsonSeason> jsonSeasons = new ArrayList<>();
    for (final Season season : seasonsOrdered) {
      final List<BonusPoint> bonusPointsForSeason = bonusPoints.stream().filter(bonusPoint -> bonusPoint.getSeason().equals(season)).collect(Collectors.toList());
      jsonSeasons.add(buildSeasonJson(season, bonusPointsForSeason));
    }
    return jsonSeasons;
  }

  public JsonSeason buildSeasonJson(final Season season, final List<BonusPoint> bonusPointsForSeason) {
    final JsonSeason jsonSeason = new JsonSeason();
    jsonSeason.setUuid(season.getUuid());
    jsonSeason.setNumber(season.getNumber());
    jsonSeason.setXpPointsType(season.getXpPointsType());
    jsonSeason.setStartDate(season.getStartDate());
    jsonSeason.setBonusPoints(buildBonusPoints(bonusPointsForSeason));
    jsonSeason.setRounds(buildRoundsJson(season.getRoundsOrdered()));

    return jsonSeason;
  }

  private ArrayList<JsonBonusPoint> buildBonusPoints(final List<BonusPoint> bonusPoints) {
    final ArrayList<JsonBonusPoint> jsonBonusPoints = new ArrayList<>();
    for (final BonusPoint bonusPoint : bonusPoints) {
      final JsonBonusPoint jsonBonusPoint = new JsonBonusPoint();
      jsonBonusPoint.setWinner(bonusPoint.getWinner().getUuid());
      jsonBonusPoint.setLooser(bonusPoint.getLooser().getUuid());
      jsonBonusPoint.setDate(bonusPoint.getDate());
      jsonBonusPoint.setUuid(bonusPoint.getUuid());
      jsonBonusPoint.setPoints(bonusPoint.getPoints());

      jsonBonusPoints.add(jsonBonusPoint);
    }
    return jsonBonusPoints;
  }

  private ArrayList<JsonRound> buildRoundsJson(final List<Round> roundsOrdered) {
    final ArrayList<JsonRound> jsonRounds = new ArrayList<>();
    for (final Round round : roundsOrdered) {
      jsonRounds.add(buildRoundJson(round));
    }
    return jsonRounds;
  }

  public JsonRound buildRoundJson(final Round round) {
    final JsonRound jsonRound = new JsonRound();
    jsonRound.setUuid(round.getUuid());
    jsonRound.setDate(round.getDate());
    jsonRound.setNumber(round.getNumber());
    jsonRound.setGroups(buildRoundGroupsJson(round));

    return jsonRound;
  }

  private ArrayList<JsonRoundGroup> buildRoundGroupsJson(final Round round) {
    final ArrayList<JsonRoundGroup> jsonRoundGroups = new ArrayList<>();
    for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
      final JsonRoundGroup jsonRoundGroup = new JsonRoundGroup();
      jsonRoundGroup.setNumber(roundGroup.getNumber());
      jsonRoundGroup.setPlayers(buildPlayersJson(roundGroup));
      jsonRoundGroup.setMatches(buildMatchesJson(roundGroup));

      jsonRoundGroups.add(jsonRoundGroup);
    }
    return jsonRoundGroups;
  }

  private ArrayList<JsonPlayer> buildPlayersJson(final RoundGroup roundGroup) {
    final Set<JsonPlayer> jsonPlayers = new LinkedHashSet<>();
    for (final Match match : roundGroup.getMatchesOrdered()) {
      final JsonPlayer jsonPlayer1 = new JsonPlayer();
      jsonPlayer1.setUuid(match.getFirstPlayer().getUuid());
      jsonPlayers.add(jsonPlayer1);

      final JsonPlayer jsonPlayer2 = new JsonPlayer();
      jsonPlayer2.setUuid(match.getSecondPlayer().getUuid());
      jsonPlayers.add(jsonPlayer2);
    }
    return new ArrayList<>(jsonPlayers);
  }

  private ArrayList<JsonMatch> buildMatchesJson(final RoundGroup roundGroup) {
    final ArrayList<JsonMatch> jsonMatches = new ArrayList<>();
    for (final Match match : roundGroup.getMatchesOrdered()) {
      final JsonMatch jsonMatch = new JsonMatch();
      jsonMatch.setFirstPlayer(match.getFirstPlayer().getUuid());
      jsonMatch.setSecondPlayer(match.getSecondPlayer().getUuid());
      jsonMatch.setSets(buildSetResultsJson(match));
      jsonMatches.add(jsonMatch);
    }
    return jsonMatches;
  }

  private ArrayList<JsonSetResult> buildSetResultsJson(final Match match) {
    final ArrayList<JsonSetResult> jsonSetResults = new ArrayList<>();
    for (final SetResult setResult : match.getSetResultsOrdered()) {
      if (isNotNull(setResult)) {
        final JsonSetResult jsonSetResult = new JsonSetResult();
        jsonSetResult.setFirstPlayerResult(setResult.getFirstPlayerScore());
        jsonSetResult.setSecondPlayerResult(setResult.getSecondPlayerScore());

        jsonSetResults.add(jsonSetResult);
      }
    }
    return jsonSetResults;
  }

  private boolean isNotNull(final SetResult setResult) {
    return setResult.getFirstPlayerScore() != null
           && setResult.getSecondPlayerScore() != null;
  }

}
