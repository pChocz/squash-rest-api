package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dbinit.fake.FakeMatch;
import com.pj.squashrestapp.dbinit.fake.FakeUtil;
import com.pj.squashrestapp.dto.match.AdditionalMatchDetailedDto;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalMatchType;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.service.AdditionalMatchService;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/additional-matches")
@RequiredArgsConstructor
public class AdditionalMatchController {

  private final AdditionalMatchService additionalMatchService;
  private final LeagueRepository leagueRepository;
  private final PlayerRepository playerRepository;
  private final AdditionalMatchRepository additionalMatchRepository;

  @PostMapping
  @PreAuthorize("""
          isOneOfThePlayers(#firstPlayerUuid, #secondPlayerUuid) 
           && hasRoleForLeague(#leagueUuid, 'PLAYER')
          """)
  void createNewAdditionalMatchEmpty(@RequestParam final UUID firstPlayerUuid,
                                     @RequestParam final UUID secondPlayerUuid,
                                     @RequestParam final UUID leagueUuid,
                                     @RequestParam final int seasonNumber,
                                     @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_FORMAT) final LocalDate date,
                                     @RequestParam final AdditionalMatchType type) {
    additionalMatchService.createNewAdditionalMatchEmpty(firstPlayerUuid, secondPlayerUuid, leagueUuid, seasonNumber, date, type);
  }

  @DeleteMapping("{matchUuid}")
  @PreAuthorize("isPlayerOfAdditionalMatch(#matchUuid)")
  void deleteMatchByUuid(@PathVariable final UUID matchUuid) {
    additionalMatchService.deleteMatchByUuid(matchUuid);
  }

  @GetMapping(value = "/{matchUuid}")
  ResponseEntity<?> getSingleMatch(@PathVariable final UUID matchUuid) {
    final AdditionalMatchDetailedDto match = additionalMatchService.getSingleMatch(matchUuid);
    return new ResponseEntity<>(match, HttpStatus.OK);
  }

  @PutMapping(value = "/{matchUuid}")
  @PreAuthorize("isPlayerOfAdditionalMatch(#matchUuid)")
  void updateSingleScore(@PathVariable final UUID matchUuid,
                         @RequestParam final int setNumber,
                         @RequestParam final String player,
                         @RequestParam final Integer newScore) {
    additionalMatchService.modifySingleScore(matchUuid, setNumber, player, newScore);
  }

  @GetMapping("/all-for-league/{leagueUuid}")
  ResponseEntity<?> getAdditionalMatchesForLeague(@PathVariable final UUID leagueUuid) {
    final List<AdditionalMatchDetailedDto> additionalMatchesForLeague = additionalMatchService.getAdditionalMatchesForLeague(leagueUuid);
    return new ResponseEntity<>(additionalMatchesForLeague, HttpStatus.OK);
  }

  @GetMapping("/single-player/{leagueUuid}/{playerUuid}")
  ResponseEntity<?> getAdditionalMatchesForSinglePlayerForLeague(@PathVariable final UUID leagueUuid,
                                                                 @PathVariable final UUID playerUuid) {
    final List<AdditionalMatchDetailedDto> additionalMatchesForSinglePlayer = additionalMatchService.getAdditionalMatchesForSinglePlayer(leagueUuid, playerUuid);
    return new ResponseEntity<>(additionalMatchesForSinglePlayer, HttpStatus.OK);
  }

  @GetMapping("/multiple-players/{leagueUuid}/{playersUuids}")
  ResponseEntity<?> getAdditionalMatchesForMultiplePlayers(@PathVariable final UUID leagueUuid,
                                                           @PathVariable final UUID[] playersUuids) {
    final List<AdditionalMatchDetailedDto> additionalMatchesForMultiplePlayers = additionalMatchService.getAdditionalMatchesForMultiplePlayers(leagueUuid, playersUuids);
    return new ResponseEntity<>(additionalMatchesForMultiplePlayers, HttpStatus.OK);
  }

  @PostMapping("create-dummy")
  void createDummyAdditionalMatches() {
    final List<League> allLeagues = leagueRepository.findAll();
    for (final League league : allLeagues) {
      final List<Player> leaguePlayers = playerRepository.fetchGeneralInfoSorted(league.getUuid(), Sort.by(Sort.Direction.ASC, "username"));

      for (final Season season : league.getSeasons()) {
        LocalDate date = season.getStartDate();
        for (int i = 0; i < 20; i++) {
          date = date.plusDays(1);
          final List<Player> twoPlayers = FakeUtil.pickTwoRandomPlayers(leaguePlayers);
          final AdditionalMatch match = FakeMatch.createAdditional(twoPlayers.get(0), twoPlayers.get(1));
          match.setDate(date);
          match.setType(AdditionalMatchType.BONUS);
          match.setSeasonNumber(season.getNumber());
          league.addAdditionalMatch(match);
        }
      }
      leagueRepository.save(league);
    }
  }

  @DeleteMapping("all")
  void deleteAllAdditionalMatches() {
    final List<AdditionalMatch> all = additionalMatchRepository.findAll();
    additionalMatchRepository.deleteAll(all);
  }


}
