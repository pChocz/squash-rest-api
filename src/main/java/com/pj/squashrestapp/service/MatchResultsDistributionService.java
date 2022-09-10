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
import java.util.Optional;
import java.util.Set;
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


    public LeagueMatchResultDistribution createDistribution(final UUID leagueUuid, final int[] seasonNumbers, final boolean includeAdditional) {
        final League league = leagueRepository.findByUuidWithSeasons(leagueUuid).orElseThrow();
        final List<MatchResultDistributionDataDto> results = matchDistributionMapper.getDistributionDataForLeague(leagueUuid, seasonNumbers, includeAdditional);
        final Map<Long, PlayerDto> players = playerRepository.findByIds(getPlayersIds(results))
                .stream()
                .collect(Collectors.toMap(Player::getId, PlayerDto::new));

        final List<PlayerMatchResultDistribution> playerMatchResultDistributionList = new ArrayList<>();

        for (Map.Entry<Long, PlayerDto> winnerEntry : players.entrySet()) {
            final Long winnerId = winnerEntry.getKey();
            final PlayerDto winner = winnerEntry.getValue();

            List<OpponentMatchResultDistribution> opponentMatchResultDistributionList = new ArrayList<>();

            for (Map.Entry<Long, PlayerDto> looserEntry : players.entrySet()) {
                final Long looserId = looserEntry.getKey();
                final PlayerDto looser = looserEntry.getValue();
                if (winnerId.equals(looserId)) {
                    continue;
                }
                final List<MatchResultDistributionDataDto> matchesDataDto = results
                        .stream()
                        .filter(v -> (Set.of(v.getWinnerId(), v.getLooserId()).equals(Set.of(winnerId, looserId))))
                        .toList();

                if (matchesDataDto.isEmpty()) {
                    continue;
                }
                Set<MatchResultCount> matchesResultCountSet = buildMatchResultCountSet(matchesDataDto, winnerId);

                final OpponentMatchResultDistribution opponentMatchResultDistribution = new OpponentMatchResultDistribution(looser, matchesResultCountSet);
                opponentMatchResultDistributionList.add(opponentMatchResultDistribution);
            }

            final PlayerMatchResultDistribution playerMatchResultDistribution = new PlayerMatchResultDistribution(winner, opponentMatchResultDistributionList);
            playerMatchResultDistributionList.add(playerMatchResultDistribution);
        }

        int allMatches = playerMatchResultDistributionList
                .stream()
                .mapToInt(PlayerMatchResultDistribution::getMatchesWon)
                .sum();

        Collections.sort(playerMatchResultDistributionList);
        return new LeagueMatchResultDistribution(new LeagueDto(league), allMatches, playerMatchResultDistributionList);
    }

    private Set<MatchResultCount> buildMatchResultCountSet(final List<MatchResultDistributionDataDto> matchesDataDto,
                                                           final Long winnerId) {
        final Set<MatchResultCount> matchResultCounts = matchesDataDto
                .stream()
                .map(dto -> new MatchResult(dto.getGamesWon(), dto.getGamesLost()))
                .map(MatchResultCount::new)
                .collect(Collectors.toSet());

        for (final MatchResultDistributionDataDto dto : matchesDataDto) {
            final Optional<MatchResultCount> optionalMatchResultCount = matchResultCounts
                    .stream()
                    .filter(v -> v.getMatchResult().getWon() == dto.getGamesWon() && v.getMatchResult().getLost() == dto.getGamesLost())
                    .findFirst();
            if (optionalMatchResultCount.isPresent()) {
                final MatchResultCount matchResultCount = optionalMatchResultCount.get();
                if (dto.getWinnerId().equals(winnerId)) {
                    matchResultCount.setMatchesWon(dto.getCount());
                } else {
                    matchResultCount.setMatchesLost(dto.getCount());
                }
            }
        }

        return matchResultCounts;
    }

    private List<Long> getPlayersIds(final List<MatchResultDistributionDataDto> results) {
        return Stream.concat(
                results.stream().map(MatchResultDistributionDataDto::getWinnerId).distinct(),
                results.stream().map(MatchResultDistributionDataDto::getLooserId).distinct()
        ).distinct().toList();
    }
}
