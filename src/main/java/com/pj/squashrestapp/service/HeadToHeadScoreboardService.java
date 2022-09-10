package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.AdditionalMatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.dto.scoreboard.headtohead.HeadToHeadScoreboard;
import com.pj.squashrestapp.dto.setresultshistogram.ReadySetResultsHistogram;
import com.pj.squashrestapp.dto.setresultshistogram.SetResultsHistogramDataDto;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.mybatis.GameDistributionMapper;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class HeadToHeadScoreboardService {

    private final GameDistributionMapper gameDistributionMapper;
    private final GameDistributionService gameDistributionService;
    private final MatchRepository matchRepository;
    private final AdditionalMatchRepository additionalMatchRepository;
    private final PlayerRepository playerRepository;

    @Cacheable(
            value = RedisCacheConfig.H2H_SCOREBOARD_CACHE,
            key = "{#firstPlayerUuid, #secondPlayerUuid, #includeAdditional}")
    public HeadToHeadScoreboard build(
            final UUID firstPlayerUuid, final UUID secondPlayerUuid, final boolean includeAdditional) {
        final UUID[] playersUuids = new UUID[] {firstPlayerUuid, secondPlayerUuid};

        final List<MatchDto> allFinishedMatches = matchRepository.fetchHeadToHead(playersUuids).stream()
                .map(MatchDetailedDto::new)
                .filter(MatchDetailedDto::checkFinished)
                .collect(Collectors.toList());

        if (includeAdditional) {
            final List<MatchDto> additionalFinishedMatches =
                    additionalMatchRepository.fetchHeadToHead(playersUuids).stream()
                            .map(AdditionalMatchDetailedDto::new)
                            .filter(AdditionalMatchDetailedDto::checkFinished)
                            .collect(Collectors.toList());
            allFinishedMatches.addAll(additionalFinishedMatches);
        }

        allFinishedMatches.sort(Comparator.comparing(MatchDto::getDate).reversed());

        List<Player> players = playerRepository.findByUuids(List.of(firstPlayerUuid, secondPlayerUuid).toArray(UUID[]::new));

        Map<Long, PlayerDto> playersMap = players.stream().collect(Collectors.toMap(Player::getId, PlayerDto::new));

        final List<SetResultsHistogramDataDto> results = gameDistributionMapper.getDistributionDataForTwoPlayers(players.get(0).getId(), players.get(1).getId(), includeAdditional);
        ReadySetResultsHistogram readySetResultsHistogram = gameDistributionService.buildDistribution(results, playersMap);

        final HeadToHeadScoreboard scoreboard = new HeadToHeadScoreboard(allFinishedMatches, readySetResultsHistogram);
        return scoreboard;
    }
}
