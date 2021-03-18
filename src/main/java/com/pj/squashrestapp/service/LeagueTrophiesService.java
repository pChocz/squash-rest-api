package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.model.dto.LeagueDtoSimple;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.TrophiesWonForLeague;
import com.pj.squashrestapp.model.dto.Trophy;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.TrophiesForLeagueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueTrophiesService {

  private final TrophiesForLeagueRepository trophiesForLeagueRepository;
  private final PlayerRepository playerRepository;
  private final LeagueRepository leagueRepository;

  @Transactional
  public List<TrophiesWonForLeague> extractTrophiesForPlayer(final UUID playerUuid) {
    final Player player = playerRepository.findByUuid(playerUuid);
    final PlayerDto playerDto = new PlayerDto(player);
    final List<TrophyForLeague> trophiesForPlayer = trophiesForLeagueRepository.findAllByPlayerUuid(playerUuid);

    final List<LeagueDtoSimple> leagues = trophiesForPlayer
            .stream()
            .map(TrophyForLeague::getLeague)
            .map(LeagueDtoSimple::new)
            .distinct()
            .collect(Collectors.toList());

    final List<TrophiesWonForLeague> trophiesWonForLeagues = new ArrayList<>();
    for (final LeagueDtoSimple league : leagues) {
      final List<TrophyForLeague> hallOfFameForLeague = trophiesForPlayer
              .stream()
              .filter(hof -> hof.getLeague().getUuid().equals(league.getLeagueUuid()))
              .collect(Collectors.toList());
      final TrophiesWonForLeague trophiesWonForLeague = new TrophiesWonForLeague(playerDto, league, hallOfFameForLeague);
      trophiesWonForLeagues.add(trophiesWonForLeague);
    }

    return trophiesWonForLeagues;
  }

  @Transactional
  public TrophyForLeague addNewTrophy(final UUID playerUuid, final UUID leagueUuid,
                                      final int seasonNumber, final Trophy trophy) {

    final League league = leagueRepository.findByUuid(leagueUuid).get();

    if (trophy.isAllowMultiple()) {
      final Player player = playerRepository.findByUuid(playerUuid);
      final Optional<TrophyForLeague> trophyForLeague = trophiesForLeagueRepository.findByLeagueAndSeasonNumberAndTrophyAndPlayer(league, seasonNumber, trophy, player);
      if (trophyForLeague.isPresent()) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Trophy of that type already exists for this player!");
      }

    } else {
      final Optional<TrophyForLeague> trophyForLeague = trophiesForLeagueRepository.findByLeagueAndSeasonNumberAndTrophy(league, seasonNumber, trophy);
      if (trophyForLeague.isPresent()) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Trophy already existing and does not allow to add more of that type!");
      }

    }

    final Player player = playerRepository.findByUuid(playerUuid);
    final TrophyForLeague newTrophyForLeague = new TrophyForLeague(seasonNumber, player, trophy);

    league.addTrophyForLeague(newTrophyForLeague);
    trophiesForLeagueRepository.save(newTrophyForLeague);

    return newTrophyForLeague;
  }

  public void removeTrophy(final UUID playerUuid, final UUID leagueUuid,
                           final int seasonNumber, final Trophy trophy) {
    final League league = leagueRepository.findByUuid(leagueUuid).get();
    final Player player = playerRepository.findByUuid(playerUuid);
    final Optional<TrophyForLeague> trophyForLeague = trophiesForLeagueRepository.findByLeagueAndSeasonNumberAndTrophyAndPlayer(league, seasonNumber, trophy, player);
    if (trophyForLeague.isPresent()) {
      trophiesForLeagueRepository.delete(trophyForLeague.get());
    } else {
      throw new NoSuchElementException("Trophy not found!");
    }
  }

}
