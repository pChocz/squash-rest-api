package com.pj.squashrestapp.dbinit.jsondto.util;

import com.pj.squashrestapp.dbinit.jsondto.JsonAdditionalMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueTrophy;
import com.pj.squashrestapp.dbinit.jsondto.JsonLostBall;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.LostBall;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.TrophyForLeague;
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
        season.setDescription(jsonSeason.getDescription());
        season.setXpPointsType(jsonSeason.getXpPointsType());
        season.setNumberOfRounds(jsonSeason.getNumberOfRounds());
        season.setRoundsToBeDeducted(jsonSeason.getRoundsToBeDeducted());
        season.setMatchFormatType(jsonSeason.getMatchFormatType());
        season.setRegularSetWinningType(jsonSeason.getRegularSetWinningType());
        season.setTiebreakWinningType(jsonSeason.getTiebreakWinningType());
        season.setRegularSetWinningPoints(jsonSeason.getRegularSetWinningPoints());
        season.setTiebreakWinningPoints(jsonSeason.getTiebreakWinningPoints());
        return season;
    }

    public Round constructRound(final JsonRound jsonRound) {
        final Round round = new Round();
        round.setNumber(jsonRound.getNumber());
        round.setDate(jsonRound.getDate());
        round.setUuid(jsonRound.getUuid());
        round.setFinished(jsonRound.isFinished());
        return round;
    }

    public RoundGroup constructRoundGroup(final JsonRoundGroup jsonRoundGroup) {
        final RoundGroup roundGroup = new RoundGroup();
        roundGroup.setNumber(jsonRoundGroup.getNumber());
        return roundGroup;
    }

    public Match constructMatch(final JsonMatch jsonMatch, final List<Player> players) {
        final Player firstPlayer = getCorrespondingPlayer(players, jsonMatch.getFirstPlayerUuid());
        final Player secondPlayer = getCorrespondingPlayer(players, jsonMatch.getSecondPlayerUuid());
        final Match match = new Match();
        match.setNumber(jsonMatch.getNumber());
        match.setFirstPlayer(firstPlayer);
        match.setSecondPlayer(secondPlayer);
        match.setMatchFormatType(jsonMatch.getMatchFormatType());
        match.setRegularSetWinningType(jsonMatch.getRegularSetWinningType());
        match.setTiebreakWinningType(jsonMatch.getTiebreakWinningType());
        match.setRegularSetWinningPoints(jsonMatch.getRegularSetWinningPoints());
        match.setTiebreakWinningPoints(jsonMatch.getTiebreakWinningPoints());
        match.setFootageLink(jsonMatch.getFootageLink());
        return match;
    }

    private Player getCorrespondingPlayer(final List<Player> players, final UUID playerUuid) {
        return players.stream()
                .filter(player -> player.getUuid().equals(playerUuid))
                .findFirst()
                .orElse(null);
    }

    public AdditionalMatch constructAdditionalMatch(
            final JsonAdditionalMatch jsonMatch, final List<Player> players, final League league) {
        final Player firstPlayer = getCorrespondingPlayer(players, jsonMatch.getFirstPlayerUuid());
        final Player secondPlayer = getCorrespondingPlayer(players, jsonMatch.getSecondPlayerUuid());
        final AdditionalMatch match = new AdditionalMatch();
        match.setFirstPlayer(firstPlayer);
        match.setSecondPlayer(secondPlayer);
        match.setMatchFormatType(jsonMatch.getMatchFormatType());
        match.setRegularSetWinningType(jsonMatch.getRegularSetWinningType());
        match.setTiebreakWinningType(jsonMatch.getTiebreakWinningType());
        match.setRegularSetWinningPoints(jsonMatch.getRegularSetWinningPoints());
        match.setTiebreakWinningPoints(jsonMatch.getTiebreakWinningPoints());
        match.setDate(jsonMatch.getDate());
        match.setType(jsonMatch.getType());
        match.setSeasonNumber(jsonMatch.getSeasonNumber());
        match.setFootageLink(jsonMatch.getFootageLink());
        return match;
    }

    public SetResult constructSetResult(final JsonSetResult jsonSetResult) {
        final SetResult setResult = new SetResult();
        setResult.setNumber(jsonSetResult.getNumber());
        setResult.setFirstPlayerScore(jsonSetResult.getFirstPlayerResult());
        setResult.setSecondPlayerScore(jsonSetResult.getSecondPlayerResult());
        return setResult;
    }

    public AdditionalSetResult constructAdditionalSetResult(final JsonSetResult jsonSetResult) {
        final AdditionalSetResult setResult = new AdditionalSetResult();
        setResult.setNumber(jsonSetResult.getNumber());
        setResult.setFirstPlayerScore(jsonSetResult.getFirstPlayerResult());
        setResult.setSecondPlayerScore(jsonSetResult.getSecondPlayerResult());
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

    public TrophyForLeague constructLeagueTrophy(final JsonLeagueTrophy jsonLeagueTrophy, final List<Player> players) {

        final Player player = players.stream()
                .filter(p -> p.getUuid().equals(jsonLeagueTrophy.getPlayerUuid()))
                .findFirst()
                .orElseThrow();

        final TrophyForLeague trophyForLeague = new TrophyForLeague();
        trophyForLeague.setSeasonNumber(jsonLeagueTrophy.getSeasonNumber());
        trophyForLeague.setPlayer(player);
        trophyForLeague.setTrophy(jsonLeagueTrophy.getTrophy());

        return trophyForLeague;
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

    public LostBall constructLostBalls(final JsonLostBall jsonLostBall, final List<Player> players) {
        final LostBall lostBall = new LostBall();
        lostBall.setUuid(jsonLostBall.getUuid());
        lostBall.setDate(jsonLostBall.getDate());
        lostBall.setCount(jsonLostBall.getCount());
        lostBall.setPlayer(getCorrespondingPlayer(players, jsonLostBall.getPlayer()));
        return lostBall;
    }

    public League constructLeague(final JsonLeague jsonLeague) {
        final League league = new League();
        league.setName(jsonLeague.getName());
        league.setTime(jsonLeague.getTime());
        league.setDateOfCreation(jsonLeague.getDateOfCreation());
        league.setLocation(jsonLeague.getLocation());
        league.setMatchFormatType(jsonLeague.getMatchFormatType());
        league.setRegularSetWinningPoints(jsonLeague.getRegularSetWinningPoints());
        league.setTiebreakWinningPoints(jsonLeague.getTiebreakWinningPoints());
        league.setRegularSetWinningType(jsonLeague.getRegularSetWinningType());
        league.setTiebreakWinningType(jsonLeague.getTiebreakWinningType());
        league.setNumberOfRoundsPerSeason(jsonLeague.getNumberOfRoundsPerSeason());
        league.setRoundsToBeDeducted(jsonLeague.getRoundsToBeDeducted());
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
