package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.RoundDto;
import com.pj.squashrestapp.dto.playerroundsstats.PlayerAllRoundsStats;
import com.pj.squashrestapp.dto.playerroundsstats.PlayerSingleRoundStats;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
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

  private final PlayerRepository playerRepository;
  private final ScoreboardService scoreboardService;

  public PlayerAllRoundsStats buildRoundsStatsForPlayer(final UUID leagueUuid, final UUID playerUuid) {
    final Player player = playerRepository.findByUuid(playerUuid);
    final PlayerDto playerDto = new PlayerDto(player);
    final List<RoundScoreboard> allRoundsScoreboards = scoreboardService.allRoundsScoreboards(leagueUuid);

    final PlayerAllRoundsStats playerAllRoundsStats = new PlayerAllRoundsStats(playerDto);

      for (final RoundScoreboard roundScoreboard : allRoundsScoreboards) {
        RoundGroupScoreboard properRoundGroupScoreboard = null;
        for (final RoundGroupScoreboard roundGroupScoreboard : roundScoreboard.getRoundGroupScoreboards()) {
          for (final RoundGroupScoreboardRow row : roundGroupScoreboard.getScoreboardRows()) {
            if (row.getPlayer().equals(playerDto)) {
              properRoundGroupScoreboard = roundGroupScoreboard;
              break;
            }
          }
        }
        if (properRoundGroupScoreboard != null) {
          final RoundDto roundDto = new RoundDto(roundScoreboard);
          playerAllRoundsStats.addSingleRoundStats(new PlayerSingleRoundStats(player, roundDto, properRoundGroupScoreboard));
        }
      }
    playerAllRoundsStats.calculateScoreboard();
      return playerAllRoundsStats;
  }
}
