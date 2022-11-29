package com.pj.squashrestapp.dbinit.jsondto.util;

import com.pj.squashrestapp.dbinit.jsondto.JsonAdditionalMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueRule;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueTrophy;
import com.pj.squashrestapp.dbinit.jsondto.JsonLostBall;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatchScore;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayer;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRule;
import com.pj.squashrestapp.model.LostBall;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.MatchScore;
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

/** */
@Slf4j
@UtilityClass
public class JsonExportUtil {

    public JsonLeague buildLeagueJson(
            final League league, final List<BonusPoint> bonusPoints, final List<LostBall> lostBalls) {
        final JsonLeague jsonLeague = new JsonLeague();
        jsonLeague.setUuid(league.getUuid());
        jsonLeague.setName(league.getName());
        jsonLeague.setTime(league.getTime());
        jsonLeague.setDateOfCreation(league.getDateOfCreation());
        jsonLeague.setLocation(league.getLocation());
        jsonLeague.setMatchFormatType(league.getMatchFormatType());
        jsonLeague.setRegularSetWinningPoints(league.getRegularSetWinningPoints());
        jsonLeague.setTiebreakWinningPoints(league.getTiebreakWinningPoints());
        jsonLeague.setRegularSetWinningType(league.getRegularSetWinningType());
        jsonLeague.setTiebreakWinningType(league.getTiebreakWinningType());
        jsonLeague.setNumberOfRoundsPerSeason(league.getNumberOfRoundsPerSeason());
        jsonLeague.setRoundsToBeDeducted(league.getRoundsToBeDeducted());
        jsonLeague.setLogoBase64(Base64.getEncoder().encodeToString(league.getLeagueLogo().getPicture()));
        jsonLeague.setRules(buildRules(league.getRules()));
        jsonLeague.setAdditionalMatches(buildAdditionalMatches(league.getAdditionalMatches()));
        jsonLeague.setSeasons(buildSeasonsJson(league.getSeasonsOrdered(), bonusPoints, lostBalls));
        jsonLeague.setTrophies(buildTrophiesList(league.getTrophiesForLeague()));

        return jsonLeague;
    }

    private ArrayList<JsonLeagueRule> buildRules(final Set<LeagueRule> leagueRules) {
        final ArrayList<JsonLeagueRule> rules = new ArrayList<>();

        for (final LeagueRule leagueRule : leagueRules) {
            final JsonLeagueRule jsonLeagueRule = new JsonLeagueRule();
            jsonLeagueRule.setUuid(leagueRule.getUuid());
            jsonLeagueRule.setOrderValue(leagueRule.getOrderValue());
            jsonLeagueRule.setType(leagueRule.getType());
            jsonLeagueRule.setRule(leagueRule.getRule());
            jsonLeagueRule.setAudit(leagueRule.getAudit());
            rules.add(jsonLeagueRule);
        }

        return rules;
    }

    private ArrayList<JsonAdditionalMatch> buildAdditionalMatches(
            final Set<AdditionalMatch> additionalMatchesForLeague) {
        final ArrayList<JsonAdditionalMatch> additionalMatches = new ArrayList<>();

        for (final AdditionalMatch additionalMatch : additionalMatchesForLeague) {
            final JsonAdditionalMatch jsonAdditionalMatch = new JsonAdditionalMatch();
            jsonAdditionalMatch.setUuid(additionalMatch.getUuid());
            jsonAdditionalMatch.setDate(additionalMatch.getDate());
            jsonAdditionalMatch.setType(additionalMatch.getType());
            jsonAdditionalMatch.setSeasonNumber(additionalMatch.getSeasonNumber());
            jsonAdditionalMatch.setFirstPlayerUuid(additionalMatch.getFirstPlayer().getUuid());
            jsonAdditionalMatch.setSecondPlayerUuid(additionalMatch.getSecondPlayer().getUuid());
            jsonAdditionalMatch.setMatchFormatType(additionalMatch.getMatchFormatType());
            jsonAdditionalMatch.setRegularSetWinningType(additionalMatch.getRegularSetWinningType());
            jsonAdditionalMatch.setTiebreakWinningType(additionalMatch.getTiebreakWinningType());
            jsonAdditionalMatch.setRegularSetWinningPoints(additionalMatch.getRegularSetWinningPoints());
            jsonAdditionalMatch.setTiebreakWinningPoints(additionalMatch.getTiebreakWinningPoints());
            jsonAdditionalMatch.setFootageLink(additionalMatch.getFootageLink());
            jsonAdditionalMatch.setAudit(additionalMatch.getAudit());
            jsonAdditionalMatch.setSets(buildSetResultsJson(additionalMatch));

            additionalMatches.add(jsonAdditionalMatch);
        }

        return additionalMatches;
    }

    private ArrayList<JsonSeason> buildSeasonsJson(
            final List<Season> seasonsOrdered, final List<BonusPoint> bonusPoints, final List<LostBall> lostBalls) {
        final ArrayList<JsonSeason> jsonSeasons = new ArrayList<>();
        for (final Season season : seasonsOrdered) {

            final List<BonusPoint> bonusPointsForSeason = bonusPoints.stream()
                    .filter(bonusPoint -> bonusPoint.getSeason().equals(season))
                    .collect(Collectors.toList());

            final List<LostBall> lostBallsForSeason = lostBalls.stream()
                    .filter(lostBall -> lostBall.getSeason().equals(season))
                    .collect(Collectors.toList());

            jsonSeasons.add(buildSeasonJson(season, bonusPointsForSeason, lostBallsForSeason));
        }
        return jsonSeasons;
    }

    private ArrayList<JsonLeagueTrophy> buildTrophiesList(final List<TrophyForLeague> trophiesForLeague) {
        final ArrayList<JsonLeagueTrophy> trophies = new ArrayList<>();

        for (final TrophyForLeague trophyForLeague : trophiesForLeague) {
            final JsonLeagueTrophy trophy = new JsonLeagueTrophy();
            trophy.setSeasonNumber(trophyForLeague.getSeasonNumber());
            trophy.setPlayerUuid(trophyForLeague.getPlayer().getUuid());
            trophy.setTrophy(trophyForLeague.getTrophy());
            trophy.setAudit(trophyForLeague.getAudit());
            trophies.add(trophy);
        }

        return trophies;
    }

    private ArrayList<JsonSetResult> buildSetResultsJson(final AdditionalMatch match) {
        final ArrayList<JsonSetResult> jsonSetResults = new ArrayList<>();
        for (final AdditionalSetResult setResult : match.getSetResultsOrdered()) {
            final JsonSetResult jsonSetResult = new JsonSetResult();
            jsonSetResult.setNumber(setResult.getNumber());
            jsonSetResult.setFirstPlayerResult(setResult.getFirstPlayerScore());
            jsonSetResult.setSecondPlayerResult(setResult.getSecondPlayerScore());
            jsonSetResults.add(jsonSetResult);
        }
        return jsonSetResults;
    }

    public JsonSeason buildSeasonJson(
            final Season season, final List<BonusPoint> bonusPointsForSeason, final List<LostBall> lostBallsForSeason) {
        final JsonSeason jsonSeason = new JsonSeason();
        jsonSeason.setUuid(season.getUuid());
        jsonSeason.setDescription(season.getDescription());
        jsonSeason.setNumber(season.getNumber());
        jsonSeason.setMatchFormatType(season.getMatchFormatType());
        jsonSeason.setRegularSetWinningType(season.getRegularSetWinningType());
        jsonSeason.setTiebreakWinningType(season.getTiebreakWinningType());
        jsonSeason.setRegularSetWinningPoints(season.getRegularSetWinningPoints());
        jsonSeason.setTiebreakWinningPoints(season.getTiebreakWinningPoints());
        jsonSeason.setNumberOfRounds(season.getNumberOfRounds());
        jsonSeason.setRoundsToBeDeducted(season.getRoundsToBeDeducted());
        jsonSeason.setXpPointsType(season.getXpPointsType());
        jsonSeason.setStartDate(season.getStartDate());
        jsonSeason.setBonusPoints(buildBonusPoints(bonusPointsForSeason));
        jsonSeason.setLostBalls(buildLostBalls(lostBallsForSeason));
        jsonSeason.setRounds(buildRoundsJson(season.getRoundsOrdered()));

        return jsonSeason;
    }

    private boolean isNotNull(final AdditionalSetResult setResult) {
        return setResult.getFirstPlayerScore() != null && setResult.getSecondPlayerScore() != null;
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
            jsonBonusPoint.setAudit(bonusPoint.getAudit());

            jsonBonusPoints.add(jsonBonusPoint);
        }
        return jsonBonusPoints;
    }

    private ArrayList<JsonLostBall> buildLostBalls(final List<LostBall> lostBalls) {
        final ArrayList<JsonLostBall> jsonLostBalls = new ArrayList<>();
        for (final LostBall lostBall : lostBalls) {
            final JsonLostBall jsonLostBall = new JsonLostBall();
            jsonLostBall.setUuid(lostBall.getUuid());
            jsonLostBall.setPlayer(lostBall.getPlayer().getUuid());
            jsonLostBall.setDate(lostBall.getDate());
            jsonLostBall.setCount(lostBall.getCount());
            jsonLostBall.setAudit(lostBall.getAudit());

            jsonLostBalls.add(jsonLostBall);
        }
        return jsonLostBalls;
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
        jsonRound.setFinished(round.isFinished());
        jsonRound.setNumber(round.getNumber());
        jsonRound.setGroups(buildRoundGroupsJson(round));
        jsonRound.setAudit(round.getAudit());

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
            jsonMatch.setUuid(match.getUuid());
            jsonMatch.setNumber(match.getNumber());
            jsonMatch.setFirstPlayerUuid(match.getFirstPlayer().getUuid());
            jsonMatch.setSecondPlayerUuid(match.getSecondPlayer().getUuid());
            jsonMatch.setMatchFormatType(match.getMatchFormatType());
            jsonMatch.setRegularSetWinningType(match.getRegularSetWinningType());
            jsonMatch.setTiebreakWinningType(match.getTiebreakWinningType());
            jsonMatch.setRegularSetWinningPoints(match.getRegularSetWinningPoints());
            jsonMatch.setTiebreakWinningPoints(match.getTiebreakWinningPoints());
            jsonMatch.setFootageLink(match.getFootageLink());
            jsonMatch.setSets(buildSetResultsJson(match));
            jsonMatch.setMatchScores(buildMatchScoresJson(match));
            jsonMatch.setAudit(match.getAudit());
            jsonMatches.add(jsonMatch);
        }
        return jsonMatches;
    }

    private ArrayList<JsonSetResult> buildSetResultsJson(final Match match) {
        final ArrayList<JsonSetResult> jsonSetResults = new ArrayList<>();
        for (final SetResult setResult : match.getSetResultsOrdered()) {
            final JsonSetResult jsonSetResult = new JsonSetResult();
            jsonSetResult.setNumber(setResult.getNumber());
            jsonSetResult.setFirstPlayerResult(setResult.getFirstPlayerScore());
            jsonSetResult.setSecondPlayerResult(setResult.getSecondPlayerScore());
            jsonSetResults.add(jsonSetResult);
        }
        return jsonSetResults;
    }

    private ArrayList<JsonMatchScore> buildMatchScoresJson(final Match match) {
        final ArrayList<JsonMatchScore> jsonMatchScores = new ArrayList<>();
        for (final MatchScore matchScore : match.getMatchScoresOrdered()) {
            final JsonMatchScore jsonMatchScore = new JsonMatchScore();
            jsonMatchScore.setGameNumber(matchScore.getGameNumber());
            jsonMatchScore.setDateTime(matchScore.getDateTime());
            jsonMatchScore.setScoreEventType(matchScore.getScoreEventType());
            jsonMatchScore.setAppealDecision(matchScore.getAppealDecision());
            jsonMatchScore.setServeSide(matchScore.getServeSide());
            jsonMatchScore.setServePlayer(matchScore.getServePlayer());
            jsonMatchScore.setNextSuggestedServePlayer(matchScore.getNextSuggestedServePlayer());
            jsonMatchScore.setFirstPlayerScore(matchScore.getFirstPlayerScore());
            jsonMatchScore.setSecondPlayerScore(matchScore.getSecondPlayerScore());
            jsonMatchScore.setFirstPlayerGamesWon(matchScore.getFirstPlayerGamesWon());
            jsonMatchScore.setSecondPlayerGamesWon(matchScore.getSecondPlayerGamesWon());
            jsonMatchScore.setCanScore(matchScore.isCanScore());
            jsonMatchScore.setCanStartGame(matchScore.isCanStartGame());
            jsonMatchScore.setCanEndGame(matchScore.isCanEndGame());
            jsonMatchScore.setCanEndMatch(matchScore.isCanEndMatch());
            jsonMatchScore.setMatchFinished(matchScore.isMatchFinished());
            jsonMatchScores.add(jsonMatchScore);
        }
        return jsonMatchScores;
    }

}
