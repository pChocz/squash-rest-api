package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.leaguestats.SeasonTrophies;
import com.pj.squashrestapp.dto.playerseasonsstats.PlayerAllSeasonsStats;
import com.pj.squashrestapp.dto.playerseasonsstats.PlayerSingleSeasonStats;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayersSeasonsStatsService {

  private final PlayerRepository playerRepository;
  private final ScoreboardService scoreboardService;
  private final LeagueTrophiesService leagueTrophiesService;

  public PlayerAllSeasonsStats buildSeasonsStatsForPlayer(final UUID leagueUuid, final UUID playerUuid) {
    final Player player = playerRepository.findByUuid(playerUuid);
    final PlayerDto playerDto = new PlayerDto(player);
    final List<SeasonScoreboardDto> allSeasonScoreboards = scoreboardService.allSeasonsScoreboards(leagueUuid);
    final List<SeasonTrophies> seasonTrophies = leagueTrophiesService.extractTrophiesForPlayerForLeague(playerDto, leagueUuid);

    final PlayerAllSeasonsStats playerAllSeasonsStats = new PlayerAllSeasonsStats(playerDto, seasonTrophies);

    for (final SeasonScoreboardDto seasonScoreboardDto : allSeasonScoreboards) {
      PlayerSingleSeasonStats playerSingleSeasonStats = null;
      for (int i=0; i < seasonScoreboardDto.getSeasonScoreboardRows().size(); i++) {
        SeasonScoreboardRowDto seasonScoreboardRow = seasonScoreboardDto.getSeasonScoreboardRows().get(i);
        if (seasonScoreboardRow.getPlayer().equals(playerDto)) {
          playerSingleSeasonStats = new PlayerSingleSeasonStats(
                  i+1,
                  seasonScoreboardDto.getSeason(),
                  seasonScoreboardRow
          );
          break;
        }
      }

      if (playerSingleSeasonStats != null) {
        playerAllSeasonsStats.addSingleSeasonStats(playerSingleSeasonStats);
      }
    }

    return playerAllSeasonsStats;
  }
}
