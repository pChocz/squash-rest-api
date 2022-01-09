package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.dto.match.AdditionalMatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.dto.scoreboard.headtohead.HeadToHeadScoreboard;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class HeadToHeadScoreboardService {

  private final MatchRepository matchRepository;
  private final AdditionalMatchRepository additionalMatchRepository;


  @Cacheable(value = RedisCacheConfig.H2H_SCOREBOARD_CACHE, key = "{#firstPlayerUuid, #secondPlayerUuid, #includeAdditional}")
  public HeadToHeadScoreboard build(final UUID firstPlayerUuid, final UUID secondPlayerUuid, final boolean includeAdditional) {
    final UUID[] playersUuids = new UUID[] {firstPlayerUuid, secondPlayerUuid};

    final List<Match> roundMatches = matchRepository.fetchHeadToHead(playersUuids);
    final List<MatchDto> allMatches = roundMatches
        .stream()
        .map(MatchDetailedDto::new)
        .collect(Collectors.toList());

    if (includeAdditional) {
      final List<AdditionalMatch> additionalMatches =
          additionalMatchRepository.fetchHeadToHead(playersUuids);
      final List<MatchDto> additionalMatchesDtos =
          additionalMatches.stream()
              .map(AdditionalMatchDetailedDto::new)
              .collect(Collectors.toList());
      allMatches.addAll(additionalMatchesDtos);
    }

    allMatches.sort(Comparator.comparing(MatchDto::getDate).reversed());

    final HeadToHeadScoreboard scoreboard = new HeadToHeadScoreboard(allMatches);
    return scoreboard;
  }
}
