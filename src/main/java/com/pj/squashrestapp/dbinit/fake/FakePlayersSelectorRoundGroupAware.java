package com.pj.squashrestapp.dbinit.fake;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.service.RoundService;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;
import com.pj.squashrestapp.service.XpPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FakePlayersSelectorRoundGroupAware {

    private final XpPointsService xpPointsService;
    private final ScoreboardService scoreboardService;
    private final SeasonService seasonService;
    private final RoundService roundService;

    public ArrayListMultimap<Integer, Player> assign(
            final List<Player> allPlayers,
            final int numberOfRoundPlayers,
            final int roundNumber,
            final int seasonNumber,
            final League league) {

        final RoundScoreboard previousRoundScoreboard =
                scoreboardService.buildMostRecentRoundOfLeague(league.getUuid());
        final int numberOfGroupsInPreviousRound =
                previousRoundScoreboard.getRoundGroupScoreboards().size();

        final UUID seasonUuid = seasonService.getCurrentSeasonUuidForLeague(league.getUuid());
        final SeasonScoreboardDto seasonScoreboard = seasonService.buildSeasonScoreboardDto(seasonUuid);
        final List<PlayerDto> playersSortedBasedOnSeasonScoreboard = seasonScoreboard.getSeasonScoreboardRows().stream()
                .map(SeasonScoreboardRowDto::getPlayer)
                .collect(Collectors.toList());

        final int numberOfGroupsInNewRound =
                switch (numberOfRoundPlayers) {
                    case 4, 5, 6, 7 -> 1;
                    case 8, 9, 10, 11, 12, 13 -> 2;
                    case 14, 15, 16 -> 3;
                    default -> throw new RuntimeException("Unsupported number of players!");
                };

        // TODO: hard case, leaving it as for now

        final ArrayListMultimap<Integer, Player> playersMap = ArrayListMultimap.create();

        for (int i = 1; i <= numberOfGroupsInPreviousRound; i++) {
            final RoundGroupScoreboard roundGroupScoreboard =
                    previousRoundScoreboard.getRoundGroupScoreboards().get(i - 1);
        }

        for (int i = 1; i <= numberOfGroupsInNewRound; i++) {}

        for (int groupNumber = 1; groupNumber <= numberOfGroupsInPreviousRound; groupNumber++) {
            final RoundGroupScoreboard roundGroupScoreboard =
                    previousRoundScoreboard.getRoundGroupScoreboards().get(groupNumber - 1);
        }

        final List<Player> group1stPlayers;
        final List<Player> group2ndPlayers;
        final List<Player> group3rdPlayers;
        final ArrayListMultimap<Integer, Player> returnMap = ArrayListMultimap.create();

        final int numberOfPlayers = allPlayers.size();

        switch (numberOfPlayers) {
            case 4, 5, 6, 7 -> {
                group1stPlayers = allPlayers;
                returnMap.putAll(1, group1stPlayers);
            }

            case 8, 9 -> {
                group1stPlayers = allPlayers.subList(0, 4);
                group2ndPlayers = allPlayers.subList(4, numberOfPlayers);
                returnMap.putAll(1, group1stPlayers);
                returnMap.putAll(2, group2ndPlayers);
            }

            case 10, 11 -> {
                group1stPlayers = allPlayers.subList(0, 5);
                group2ndPlayers = allPlayers.subList(5, numberOfPlayers);
                returnMap.putAll(1, group1stPlayers);
                returnMap.putAll(2, group2ndPlayers);
            }

            case 12, 13 -> {
                group1stPlayers = allPlayers.subList(0, 6);
                group2ndPlayers = allPlayers.subList(6, numberOfPlayers);
                returnMap.putAll(1, group1stPlayers);
                returnMap.putAll(2, group2ndPlayers);
            }

            case 14, 15 -> {
                group1stPlayers = allPlayers.subList(0, 5);
                group2ndPlayers = allPlayers.subList(5, 10);
                group3rdPlayers = allPlayers.subList(10, numberOfPlayers);
                returnMap.putAll(1, group1stPlayers);
                returnMap.putAll(2, group2ndPlayers);
                returnMap.putAll(3, group3rdPlayers);
            }

            case 16 -> {
                group1stPlayers = allPlayers.subList(0, 6);
                group2ndPlayers = allPlayers.subList(6, 11);
                group3rdPlayers = allPlayers.subList(11, numberOfPlayers);
                returnMap.putAll(1, group1stPlayers);
                returnMap.putAll(2, group2ndPlayers);
                returnMap.putAll(3, group3rdPlayers);
            }
        }

        return returnMap;
    }
}
