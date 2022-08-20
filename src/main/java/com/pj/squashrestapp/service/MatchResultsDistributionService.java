package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.matchresultsdistribution.MatchResultCount;
import com.pj.squashrestapp.dto.matchresultsdistribution.MatchResult;
import com.pj.squashrestapp.dto.matchresultsdistribution.MatchResultDistributionDataDto;
import com.pj.squashrestapp.dto.matchresultsdistribution.OpponentMatchResultDistribution;
import com.pj.squashrestapp.dto.matchresultsdistribution.LeagueMatchResultDistribution;
import com.pj.squashrestapp.dto.matchresultsdistribution.PlayerMatchResultDistribution;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.mybatis.MatchDistributionMapper;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchResultsDistributionService {

    private final MatchDistributionMapper matchDistributionMapper;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;


    public LeagueMatchResultDistribution createDistribution(final UUID leagueUuid, final int[] seasonNumbers) {
        final League league = leagueRepository.findByUuidWithSeasons(leagueUuid).orElseThrow();
        final List<MatchResultDistributionDataDto> results = matchDistributionMapper.getDistributionData(leagueUuid, seasonNumbers);
        final Map<Long, PlayerDto> players = playerRepository.findByIds(getPlayersIds(results))
                .stream()
                .collect(Collectors.toMap(Player::getId, PlayerDto::new));

        List<PlayerMatchResultDistribution> playerMatchResultDistributionList = new ArrayList<>();

        for (Map.Entry<Long, PlayerDto> winnerEntry : players.entrySet()) {
            final Long winnerId = winnerEntry.getKey();
            final PlayerDto winner = winnerEntry.getValue();

            List<OpponentMatchResultDistribution> opponentMatchResultDistributionList = new ArrayList<>();

            for (Map.Entry<Long, PlayerDto> looserEntry : players.entrySet()) {
                final Long looserId = looserEntry.getKey();
                final PlayerDto looser = looserEntry.getValue();
                final List<MatchResultDistributionDataDto> matchResultDistributionDataDtos = results
                        .stream()
                        .filter(v -> (v.getWinnerId().equals(winnerId) && v.getLooserId().equals(looserId)))
                        .toList();
                if (matchResultDistributionDataDtos.isEmpty()) {
                    continue;
                }
                Set<MatchResultCount> matchResultCountList = new TreeSet<>();
                for (final MatchResultDistributionDataDto dto : matchResultDistributionDataDtos) {
                    final MatchResult matchResult = new MatchResult(dto.getGamesWon(), dto.getGamesLost());
                    matchResultCountList.add(new MatchResultCount(matchResult, dto.getCount()));
                }

                final OpponentMatchResultDistribution opponentMatchResultDistribution = new OpponentMatchResultDistribution(looser, matchResultCountList);
                opponentMatchResultDistributionList.add(opponentMatchResultDistribution);
            }

            final PlayerMatchResultDistribution playerMatchResultDistribution = new PlayerMatchResultDistribution(winner, opponentMatchResultDistributionList);
            playerMatchResultDistributionList.add(playerMatchResultDistribution);
        }

        Collections.sort(playerMatchResultDistributionList);
        return new LeagueMatchResultDistribution(new LeagueDto(league), playerMatchResultDistributionList);
    }

    private List<Long> getPlayersIds(final List<MatchResultDistributionDataDto> results) {
        return Stream.concat(
                results.stream().map(MatchResultDistributionDataDto::getWinnerId).distinct(),
                results.stream().map(MatchResultDistributionDataDto::getLooserId).distinct()
        ).distinct().toList();
    }
}
