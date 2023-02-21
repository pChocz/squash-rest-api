package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.encounters.PlayersEncountersStats;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundLeagueUuidDto;
import com.pj.squashrestapp.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayersEncountersService {

    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;
    private final ScoreboardService scoreboardService;

    public PlayersEncountersStats build(final UUID firstPlayerUuid, final UUID secondPlayerUuid) {
        final List<Player> players = playerRepository.findByUuids(new UUID[] {firstPlayerUuid, secondPlayerUuid});
        if (players.size() != 2) {
            return new PlayersEncountersStats();
        }
        final List<RoundLeagueUuidDto> roundLeagueUuidDtoList = roundRepository.findAllForPlayersEncounters(firstPlayerUuid, secondPlayerUuid);
        final Set<UUID> leaguesUuids = extractAllLeaguesUuids(roundLeagueUuidDtoList);
        final Set<UUID> roundsUuids = extractAllRoundsUuids(roundLeagueUuidDtoList);
        final List<RoundScoreboard> allRoundsScoreboardsForLeagues = extractAllRoundsScoreboardsForAllLeagues(leaguesUuids);
        final List<RoundScoreboard> properRoundsScoreboards = extractMatchingRoundsOnly(roundsUuids, allRoundsScoreboardsForLeagues);
        return new PlayersEncountersStats(properRoundsScoreboards, players);
    }

    private static Set<UUID> extractAllLeaguesUuids(final List<RoundLeagueUuidDto> roundLeagueUuidDtoList) {
        return roundLeagueUuidDtoList.stream()
                .map(RoundLeagueUuidDto::getLeagueUuid)
                .collect(Collectors.toSet());
    }

    private static TreeSet<UUID> extractAllRoundsUuids(final List<RoundLeagueUuidDto> roundLeagueUuidDtoList) {
        return roundLeagueUuidDtoList.stream()
                .map(RoundLeagueUuidDto::getRoundUuid)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private List<RoundScoreboard> extractAllRoundsScoreboardsForAllLeagues(final Set<UUID> leaguesUuids) {
        return leaguesUuids.stream()
                .map(scoreboardService::allRoundsScoreboards)
                .flatMap(Collection::stream)
                .toList();
    }

    private static List<RoundScoreboard> extractMatchingRoundsOnly(final Set<UUID> roundsUuids, final List<RoundScoreboard> allRoundsScoreboardsForLeagues) {
        return allRoundsScoreboardsForLeagues.stream()
                .filter(r -> roundsUuids.contains(r.getRoundUuid()))
                .sorted(Comparator.comparing(RoundScoreboard::getRoundDate))
                .toList();
    }
}
