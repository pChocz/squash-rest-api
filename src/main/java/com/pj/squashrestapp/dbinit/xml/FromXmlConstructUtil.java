package com.pj.squashrestapp.dbinit.xml;

import com.pj.squashrestapp.dbinit.xml.entities.XmlBonus;
import com.pj.squashrestapp.dbinit.xml.entities.XmlGroup;
import com.pj.squashrestapp.dbinit.xml.entities.XmlHallOfFameSeason;
import com.pj.squashrestapp.dbinit.xml.entities.XmlLeague;
import com.pj.squashrestapp.dbinit.xml.entities.XmlMatch;
import com.pj.squashrestapp.dbinit.xml.entities.XmlRound;
import com.pj.squashrestapp.dbinit.xml.entities.XmlSeason;
import com.pj.squashrestapp.dbinit.xml.entities.XmlSet;
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
public class FromXmlConstructUtil {

  public Season constructSeason(final XmlSeason xmlSeason) {
    final Season season = new Season();
    season.setNumber(xmlSeason.getId());
    season.setStartDate(constructLocalDate(xmlSeason.getStartDate()));
    return season;
  }

  private LocalDate constructLocalDate(final String dateAsString) {
    final String[] partsOfDate = dateAsString.split("\\.");
    final String properDateString = partsOfDate[2] + "-" + partsOfDate[1] + "-" + partsOfDate[0];
    return LocalDate.parse(properDateString);
  }

  public Round constructRound(final XmlRound xmlRound) {
    final Round round = new Round();
    round.setNumber(xmlRound.getNumber());
    round.setDate(constructLocalDate(xmlRound.getDate()));
    round.setFinished(true);
    return round;
  }

  public RoundGroup constructRoundGroup(final XmlGroup xmlGroup) {
    final RoundGroup roundGroup = new RoundGroup();
    roundGroup.setNumber(xmlGroup.getId());
    return roundGroup;
  }

  public Match constructMatch(final XmlMatch xmlMatch, final List<Player> players) {
    final Player firstPlayer = getCorrespondingPlayer(players, xmlMatch.getFirstPlayer());
    final Player secondPlayer = getCorrespondingPlayer(players, xmlMatch.getSecondPlayer());
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

  public SetResult constructSetResult(final int setNumber, final XmlSet xmlSet) {
    final SetResult setResult = new SetResult();
    setResult.setNumber(setNumber);
    setResult.setFirstPlayerScore(xmlSet.getFirstPlayerResult());
    setResult.setSecondPlayerScore(xmlSet.getSecondPlayerResult());
    return setResult;
  }

  public SetResult constructEmptySetResult(final int setNumber) {
    final SetResult setResult = new SetResult();
    setResult.setNumber(setNumber);
    setResult.setFirstPlayerScore(0);
    setResult.setSecondPlayerScore(0);
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

  public HallOfFameSeason constructHallOfFameSeason(final XmlHallOfFameSeason xmlHallOfFameSeason) {
    final HallOfFameSeason hallOfFameSeason = new HallOfFameSeason();
    hallOfFameSeason.setSeasonNumber(xmlHallOfFameSeason.getSeasonNumber());
    hallOfFameSeason.setLeague1stPlace(xmlHallOfFameSeason.getLeague1stPlace());
    hallOfFameSeason.setLeague2ndPlace(xmlHallOfFameSeason.getLeague2ndPlace());
    hallOfFameSeason.setLeague3rdPlace(xmlHallOfFameSeason.getLeague3rdPlace());
    hallOfFameSeason.setCup1stPlace(xmlHallOfFameSeason.getCup1stPlace());
    hallOfFameSeason.setCup2ndPlace(xmlHallOfFameSeason.getCup2ndPlace());
    hallOfFameSeason.setCup3rdPlace(xmlHallOfFameSeason.getCup3rdPlace());
    hallOfFameSeason.setSuperCupWinner(xmlHallOfFameSeason.getSuperCupWinner());
    hallOfFameSeason.setPretendersCupWinner(xmlHallOfFameSeason.getPretendersCupWinner());
    return hallOfFameSeason;
  }

  public BonusPoint constructBonusPoints(final XmlBonus xmlBonus, final List<Player> players) {
    final BonusPoint bonusPoint = new BonusPoint();
    bonusPoint.setPlayer(getCorrespondingPlayer(players, xmlBonus.getPlayerName()));
    bonusPoint.setPoints(xmlBonus.getPoints());
    return bonusPoint;
  }

  public League constructLeague(final XmlLeague xmlLeague) {
    final League league = new League();
    league.setName(xmlLeague.getName());
    league.setLeagueLogo(constructLeagueLogo(xmlLeague));
    return league;
  }

  private LeagueLogo constructLeagueLogo(final XmlLeague xmlLeague) {
    final LeagueLogo leagueLogo = new LeagueLogo();
    final byte[] logoBytes = Base64.getDecoder().decode(xmlLeague.getLogoBase64());
    leagueLogo.setPicture(logoBytes);
    return leagueLogo;
  }

}
