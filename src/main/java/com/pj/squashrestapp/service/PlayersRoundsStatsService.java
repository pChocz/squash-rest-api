package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.playerroundsstats.PlayerAllRoundsStats;
import com.pj.squashrestapp.dto.playerroundsstats.PlayerSingleRoundStats;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundGroupRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayersRoundsStatsService {

  private final XpPointsService xpPointsService;

  private final LeagueRepository leagueRepository;
  private final SetResultRepository setResultRepository;
  private final PlayerRepository playerRepository;
  private final RoundGroupRepository roundGroupRepository;

  public PlayerAllRoundsStats buildRoundsStatsForPlayer(
      final UUID leagueUuid, final UUID playerUuid) {
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final Player player = playerRepository.findByUuid(playerUuid);

    final List<Long> roundGroupsIds =
        roundGroupRepository.retrieveRoundGroupsIdsForPlayer(leagueUuid, playerUuid);
    final List<SetResult> setResults = setResultRepository.fetchByRoundGroupsIds(roundGroupsIds);

    final ArrayListMultimap<String, Integer> xpPointsPerSplit =
        xpPointsService.buildAllAsIntegerMultimap();

    final League leagueReconstructed =
        EntityGraphBuildUtil.reconstructLeague(setResults, league.getId());

    final PlayerAllRoundsStats playerAllRoundsStats = new PlayerAllRoundsStats(new PlayerDto(player));

    if (leagueReconstructed != null) {
      for (final Season season : leagueReconstructed.getSeasons()) {
        for (final Round round : season.getRounds()) {
          final List<Integer> xpPoints =
              xpPointsPerSplit.get(round.getSplit() + "|" + season.getXpPointsType());
          playerAllRoundsStats.addSingleRoundStats(new PlayerSingleRoundStats(player, round, xpPoints));
        }
      }
    }
    Collections.reverse(playerAllRoundsStats.getPlayerSingleRoundStats());
    playerAllRoundsStats.calculateScoreboard();
    return playerAllRoundsStats;
  }
}
