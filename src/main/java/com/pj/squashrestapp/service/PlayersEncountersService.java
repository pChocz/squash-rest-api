package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.dto.encounters.PlayersEncountersStats;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/* */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayersEncountersService {

    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;
    private final XpPointsService xpPointsService;

    public PlayersEncountersStats build(final UUID firstPlayerUuid, final UUID secondPlayerUuid) {
        final List<Player> players = playerRepository.findByUuids(new UUID[]{firstPlayerUuid, secondPlayerUuid});
        if (players.size() != 2) {
            return new PlayersEncountersStats();
        }
        final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();
        final List<Long> roundIds = roundRepository.findRoundsForEncounter(firstPlayerUuid, secondPlayerUuid);
        final List<Round> roundsForEncountersStats = roundRepository.findByIdsWithSeasonLeague(roundIds);
        final List<RoundScoreboard> roundScoreboards = roundsForEncountersStats
                .stream()
                .sorted(Comparator.comparing(Round::getDate))
                .map(round -> {
                    final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
                    final String split = round.getSplit();
                    final String type = round.getSeason().getXpPointsType();
                    final List<Integer> xpPoints = xpPointsPerSplit.get(split + '|' + type);
                    roundScoreboard.assignPointsAndPlaces(xpPoints);
                    return roundScoreboard;
                })
                .filter(RoundScoreboard::isFinishedState)
                .toList();
        return new PlayersEncountersStats(roundScoreboards, players);
    }
}
