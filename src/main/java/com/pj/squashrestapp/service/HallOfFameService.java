package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.LeagueDtoSimple;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.TrophiesWonForLeague;
import com.pj.squashrestapp.repository.HallOfFameSeasonRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HallOfFameService {

  private final HallOfFameSeasonRepository hallOfFameSeasonRepository;
  private final PlayerRepository playerRepository;

  public List<TrophiesWonForLeague> extractHallOfFameForPlayer(final UUID playerUuid) {
    final Player player = playerRepository.findByUuid(playerUuid);
    final PlayerDto playerDto = new PlayerDto(player);
    final List<HallOfFameSeason> hallOfFameForPlayer = hallOfFameSeasonRepository.findAllByPlayerName(player.getUsername());

    final List<LeagueDtoSimple> leagues = hallOfFameForPlayer
            .stream()
            .map(HallOfFameSeason::getLeague)
            .map(LeagueDtoSimple::new)
            .distinct()
            .collect(Collectors.toList());

    final List<TrophiesWonForLeague> trophiesWonForLeagues = new ArrayList<>();
    for (final LeagueDtoSimple league : leagues) {
      final List<HallOfFameSeason> hallOfFameForLeague = hallOfFameForPlayer
              .stream()
              .filter(hof -> hof.getLeague().getUuid().equals(league.getLeagueUuid()))
              .collect(Collectors.toList());
      final TrophiesWonForLeague trophiesWonForLeague = new TrophiesWonForLeague(playerDto, league, hallOfFameForLeague);
      trophiesWonForLeagues.add(trophiesWonForLeague);
    }

    return trophiesWonForLeagues;
  }

}
