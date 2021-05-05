package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;
import com.pj.squashrestapp.dto.match.AdditionalMatchDetailedDto;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalMatchType;
import com.pj.squashrestapp.model.AdditonalSetResult;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.AdditionalSetResultRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdditionalMatchService {

  private final AdditionalMatchRepository additionalMatchRepository;
  private final AdditionalSetResultRepository additonalSetResultRepository;
  private final PlayerRepository playerRepository;
  private final LeagueRepository leagueRepository;

  public List<AdditionalMatchDetailedDto> getAdditionalMatchesForLeague(final UUID leagueUuid) {
    final Optional<League> league = leagueRepository.findByUuid(leagueUuid);
    if (league.isEmpty()) {
      throw new GeneralBadRequestException("League not valid!");
    }
    final List<AdditionalMatch> matches = additionalMatchRepository.findAllByLeagueOrderByDateDescIdDesc(league.get());
    return buildDtoList(matches);
  }

  private static List<AdditionalMatchDetailedDto> buildDtoList(final List<AdditionalMatch> matches) {
    return matches
            .stream()
            .map(AdditionalMatchDetailedDto::new)
            .collect(Collectors.toList());
  }

  public List<AdditionalMatchDetailedDto> getAdditionalMatchesForSinglePlayer(final UUID leagueUuid, final UUID playerUuid) {

    final Player player = playerRepository.findByUuid(playerUuid);
    if (player == null) {
      throw new GeneralBadRequestException("Player not found!");
    }

    final Optional<League> league = leagueRepository.findByUuid(leagueUuid);
    if (league.isEmpty()) {
      throw new GeneralBadRequestException("League not valid!");
    }

    final List<AdditionalMatch> matches = additionalMatchRepository.fetchForSinglePlayerForLeague(player, league.get());
    return buildDtoList(matches);
  }

  public List<AdditionalMatchDetailedDto> getAdditionalMatchesForMultiplePlayers(final UUID leagueUuid, final UUID[] playersUuids) {
    final Optional<League> league = leagueRepository.findByUuid(leagueUuid);
    if (league.isEmpty()) {
      throw new GeneralBadRequestException("League not valid!");
    }
    final List<AdditionalMatch> matches = additionalMatchRepository.fetchForMultiplePlayersForLeague(playersUuids, league.get());
    return buildDtoList(matches);
  }

  @Transactional
  public void createNewAdditionalMatchEmpty(final UUID firstPlayerUuid, final UUID secondPlayerUuid,
                                            final UUID leagueUuid, final int seasonNumber,
                                            final LocalDate date, final AdditionalMatchType type) {

    final Player firstPlayer = playerRepository.findByUuid(firstPlayerUuid);
    final Player secondPlayer = playerRepository.findByUuid(secondPlayerUuid);
    if (firstPlayer == null || secondPlayer == null) {
      throw new GeneralBadRequestException("Players not valid!");
    }

    final Optional<League> league = leagueRepository.findByUuid(leagueUuid);
    if (league.isEmpty()) {
      throw new GeneralBadRequestException("League not valid!");
    }

    final AdditionalMatch match = new AdditionalMatch();
    match.setFirstPlayer(firstPlayer);
    match.setSecondPlayer(secondPlayer);
    match.setDate(date);
    match.setType(type);
    match.setSeasonNumber(seasonNumber);

    for (int setNumber = 1; setNumber <= 3; setNumber++) {
      final AdditonalSetResult setResult = new AdditonalSetResult();
      setResult.setNumber(setNumber);
      setResult.setFirstPlayerScore(null);
      setResult.setSecondPlayerScore(null);

      match.addSetResult(setResult);
    }

    league.get().addAdditionalMatch(match);
  }

  public void deleteMatchByUuid(final UUID matchUuid) {
    final Optional<AdditionalMatch> match = additionalMatchRepository.findByUuid(matchUuid);
    if (match.isEmpty()) {
      throw new GeneralBadRequestException("Additional match not found!");
    }
    additionalMatchRepository.delete(match.get());
  }

  public void modifySingleScore(final UUID matchUuid, final int setNumber, final String player, final Integer looserScore) {
    final AdditionalMatch matchToModify = additionalMatchRepository.findByUuid(matchUuid).orElseThrow();

    final String initialMatchResult = matchToModify.toString();

    final AdditonalSetResult setToModify = matchToModify
            .getSetResults()
            .stream()
            .filter(set -> set.getNumber() == setNumber)
            .findFirst()
            .orElse(null);

    if (looserScore == -1) {
      setToModify.setFirstPlayerScore(null);
      setToModify.setSecondPlayerScore(null);

    } else {
      final Integer winnerScore = computeWinnerScore(looserScore, setNumber);

      if (player.equals("FIRST")) {
        setToModify.setFirstPlayerScore(looserScore);
        setToModify.setSecondPlayerScore(winnerScore);

      } else if (player.equals("SECOND")) {
        setToModify.setFirstPlayerScore(winnerScore);
        setToModify.setSecondPlayerScore(looserScore);
      }
    }

    additonalSetResultRepository.save(setToModify);

    log.info("Succesfully updated additional match!\n\t-> {}\t- earlier\n\t-> {}\t- now", initialMatchResult, matchToModify);
  }

  private Integer computeWinnerScore(final Integer looserScore, final int setNumber) {
    if (setNumber < 3) {
      return computeWinnerScoreForRegularSet(looserScore);
    } else {
      return 9;
    }
  }

  private Integer computeWinnerScoreForRegularSet(final Integer looserScore) {
    if (looserScore < 10) {
      return 11;
    } else {
      return 12;
    }
  }

  public AdditionalMatchDetailedDto getSingleMatch(final UUID matchUuid) {
    final Optional<AdditionalMatch> matchOptional = additionalMatchRepository.findByUuid(matchUuid);
    if (matchOptional.isPresent()) {
      return new AdditionalMatchDetailedDto(matchOptional.get());
    } else {
      throw new GeneralBadRequestException("No such match!");
    }
  }

}
