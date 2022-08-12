package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.AdditionalMatchSimpleDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.scoreboard.PlayerSummary;
import com.pj.squashrestapp.dto.scoreboard.PlayersStatsScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayersScoreboardService {

    private final MatchRepository matchRepository;
    private final AdditionalMatchRepository additionalMatchRepository;
    private final PlayerRepository playerRepository;
    private final SeasonRepository seasonRepository;
    private final LeagueRepository leagueRepository;

    public Scoreboard buildSingle(
            final UUID leagueUuid,
            final UUID playerUuid,
            final UUID seasonUuid,
            final Integer groupNumber,
            final LocalDate dateFrom,
            final LocalDate dateTo,
            final boolean includeAdditionalMatches) {

        final List<Match> matches = matchRepository.fetchForOnePlayerForLeagueForSeasonForGroupNumber(
                leagueUuid, playerUuid, seasonUuid, dateFrom, dateTo, groupNumber);

        final List<MatchDto> roundMatchesDtos = matches.stream()
                .map(MatchSimpleDto::new)
                .filter(MatchSimpleDto::checkFinished)
                .collect(Collectors.toList());

        final Collection<MatchDto> allMatches = new ArrayList<>(roundMatchesDtos);

        if (includeAdditionalMatches) {
            final Integer seasonNumber = seasonUuid == null
                    ? null
                    : seasonRepository.findByUuid(seasonUuid).get().getNumber();

            final List<AdditionalMatch> additionalMatches =
                    additionalMatchRepository.fetchForSinglePlayerForLeagueForSeasonNumber(
                            leagueUuid, playerUuid, dateFrom, dateTo, seasonNumber);

            final List<MatchDto> additionalMatchesDtos = additionalMatches.stream()
                    .map(AdditionalMatchSimpleDto::new)
                    .filter(AdditionalMatchSimpleDto::checkFinished)
                    .collect(Collectors.toList());

            allMatches.addAll(additionalMatchesDtos);
        }

        final Scoreboard scoreboard = new Scoreboard(allMatches);
        scoreboard.makeItSinglePlayerScoreboard(playerUuid);

        return scoreboard;
    }

    public Scoreboard buildMultipleAllAgainstAll(
            final UUID leagueUuid,
            final UUID[] playersUuids,
            final UUID seasonUuid,
            final Integer groupNumber,
            final LocalDate dateFrom,
            final LocalDate dateTo,
            final boolean includeAdditionalMatches) {

        final List<Match> roundMatches = matchRepository.fetchForSeveralPlayersForLeagueForSeasonForGroupNumber(
                leagueUuid, playersUuids, seasonUuid, dateFrom, dateTo, groupNumber);

        final List<MatchDto> roundMatchesDtos = roundMatches.stream()
                .map(MatchSimpleDto::new)
                .filter(MatchSimpleDto::checkFinished)
                .collect(Collectors.toList());

        final Collection<MatchDto> allMatches = new ArrayList<>();
        allMatches.addAll(roundMatchesDtos);

        if (includeAdditionalMatches) {
            final Integer seasonNumber = seasonUuid == null
                    ? null
                    : seasonRepository.findByUuid(seasonUuid).get().getNumber();

            final List<AdditionalMatch> additionalMatches =
                    additionalMatchRepository.fetchForSeveralPlayersForLeagueForSeasonNumber(
                            leagueUuid, playersUuids, dateFrom, dateTo, seasonNumber);

            final List<MatchDto> additionalMatchesDtos = additionalMatches.stream()
                    .map(AdditionalMatchSimpleDto::new)
                    .filter(AdditionalMatchSimpleDto::checkFinished)
                    .collect(Collectors.toList());

            allMatches.addAll(additionalMatchesDtos);
        }

        final Scoreboard scoreboard = new Scoreboard(allMatches);
        return scoreboard;
    }

    @Cacheable(value = RedisCacheConfig.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, key = "#playerUuid")
    public PlayerSummary buildPlayerAgainstAllForAllLeagues(final UUID playerUuid) {
        final Player player = playerRepository.findByUuid(playerUuid);
        final PlayerDto playerDto = new PlayerDto(player);
        final List<Match> roundMatches = matchRepository.fetchByOnePlayerAgainstAllForAllLeagues(playerUuid);
        final List<AdditionalMatch> additionalMatches = additionalMatchRepository.fetchAllForSinglePlayer(player);

        final List<MatchDto> roundMatchesDtos = roundMatches.stream()
                .map(MatchDetailedDto::new)
                .filter(MatchDetailedDto::checkFinished)
                .collect(Collectors.toList());

        final List<MatchDto> additionalMatchesDtos = additionalMatches.stream()
                .map(AdditionalMatchSimpleDto::new)
                .filter(AdditionalMatchSimpleDto::checkFinished)
                .collect(Collectors.toList());

        final List<MatchDto> allMatchesDtos = new ArrayList<>();
        allMatchesDtos.addAll(roundMatchesDtos);
        allMatchesDtos.addAll(additionalMatchesDtos);

        final Set<UUID> leagues = new HashSet<>();
        final Set<UUID> seasons = new HashSet<>();
        final Set<UUID> rounds = new HashSet<>();

        for (final Match match : roundMatches) {
            leagues.add(match.getRoundGroup().getRound().getSeason().getLeague().getUuid());
            seasons.add(match.getRoundGroup().getRound().getSeason().getUuid());
            rounds.add(match.getRoundGroup().getRound().getUuid());
        }

        final Scoreboard scoreboard = new Scoreboard(allMatchesDtos);
        final PlayersStatsScoreboardRow scoreboardRow = scoreboard.getRowForPlayer(playerDto);

        final PlayerSummary playerSummary =
                new PlayerSummary(scoreboardRow, leagues.size(), seasons.size(), rounds.size());

        return playerSummary;
    }

    @Cacheable(value = RedisCacheConfig.PLAYER_LEAGUE_SCOREBOARD_CACHE, key = "{#leagueUuid, #playerUuid}")
    public Scoreboard buildMultiplePlayerAgainstAll(final UUID leagueUuid, final UUID playerUuid) {
        final Player player = playerRepository.findByUuid(playerUuid);
        final League league = leagueRepository.findByUuid(leagueUuid).get();

        final List<Match> matches = matchRepository.fetchByOnePlayerAgainstOthersAndLeagueId(leagueUuid, playerUuid);

        final List<AdditionalMatch> additionalMatches =
                additionalMatchRepository.fetchForSinglePlayerForLeague(player, league);

        final List<MatchDto> roundMatchesDtos = matches.stream()
                .map(MatchSimpleDto::new)
                .filter(MatchSimpleDto::checkFinished)
                .collect(Collectors.toList());

        final List<MatchDto> additionalMatchesDtos = additionalMatches.stream()
                .map(AdditionalMatchSimpleDto::new)
                .filter(AdditionalMatchSimpleDto::checkFinished)
                .collect(Collectors.toList());

        final List<MatchDto> allMatchesDtos = new ArrayList<>();
        allMatchesDtos.addAll(roundMatchesDtos);
        allMatchesDtos.addAll(additionalMatchesDtos);

        final Scoreboard scoreboard = new Scoreboard(allMatchesDtos);
        scoreboard.removeSinglePlayer(playerUuid);
        scoreboard.reverse();

        return scoreboard;
    }
}
