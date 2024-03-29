package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.LeagueDtoSimple;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.SeasonDto;
import com.pj.squashrestapp.dto.TrophiesWonForLeague;
import com.pj.squashrestapp.dto.Trophy;
import com.pj.squashrestapp.dto.leaguestats.SeasonTrophies;
import com.pj.squashrestapp.dto.leaguestats.TrophyDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.TrophiesForLeagueRepository;
import com.pj.squashrestapp.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueTrophiesService {

    private final TrophiesForLeagueRepository trophiesForLeagueRepository;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;
    private final LeagueService leagueService;

    @Transactional(readOnly = true)
    public List<SeasonTrophies> extractTrophiesForPlayerForLeague(final PlayerDto playerDto, final UUID leagueUuid) {
        final List<TrophyForLeague> trophiesForPlayer =
                trophiesForLeagueRepository.findAllByPlayerUuidAndLeagueUuid(playerDto.getUuid(), leagueUuid);
        final LeagueDto leagueGeneralInfo = leagueService.buildGeneralInfoForLeague(leagueUuid);
        final List<Integer> seasonNumbers = leagueGeneralInfo.getSeasons()
                .stream()
                .map(SeasonDto::getSeasonNumber)
                .toList();
        final List<SeasonTrophies> leagueTrophiesPerSeason = buildSeasonTrophies(trophiesForPlayer, seasonNumbers);

        return leagueTrophiesPerSeason;
    }

    private List<SeasonTrophies> buildSeasonTrophies(List<TrophyForLeague> trophiesForPlayer, final List<Integer> seasonNumbers) {
        final List<SeasonTrophies> leagueTrophiesPerSeason = new ArrayList<>();
        final List<Integer> listOfSeasonNumbers = Stream
                .concat(
                        trophiesForPlayer.stream().map(TrophyForLeague::getSeasonNumber),
                        seasonNumbers.stream())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        for (final int seasonNumber : listOfSeasonNumbers) {
            final List<TrophyForLeague> seasonTrophies = trophiesForPlayer.stream()
                    .filter(trophyForLeague -> trophyForLeague.getSeasonNumber() == seasonNumber)
                    .sorted(Comparator.comparingInt(o -> o.getTrophy().ordinal()))
                    .collect(Collectors.toList());
            leagueTrophiesPerSeason.add(new SeasonTrophies(seasonNumber, seasonTrophies));
        }
        return leagueTrophiesPerSeason;
    }

    @Transactional(readOnly = true)
    public List<TrophiesWonForLeague> extractTrophiesForPlayer(final UUID playerUuid) {
        final Player player = playerRepository.findByUuid(playerUuid);
        final PlayerDto playerDto = new PlayerDto(player);
        final List<TrophyForLeague> trophiesForPlayer = trophiesForLeagueRepository.findAllByPlayerUuid(playerUuid);

        final List<LeagueDtoSimple> leagues = trophiesForPlayer.stream()
                .map(TrophyForLeague::getLeague)
                .map(LeagueDtoSimple::new)
                .distinct()
                .collect(Collectors.toList());

        final List<TrophiesWonForLeague> trophiesWonForLeagues = new ArrayList<>();
        for (final LeagueDtoSimple league : leagues) {
            final List<TrophyForLeague> hallOfFameForLeague = trophiesForPlayer.stream()
                    .filter(hof -> hof.getLeague().getUuid().equals(league.getLeagueUuid()))
                    .collect(Collectors.toList());
            final TrophiesWonForLeague trophiesWonForLeague =
                    new TrophiesWonForLeague(playerDto, league, hallOfFameForLeague);
            trophiesWonForLeagues.add(trophiesWonForLeague);
        }

        return trophiesWonForLeagues;
    }

    @Transactional
    public TrophyForLeague addNewTrophy(
            final UUID playerUuid, final UUID leagueUuid, final int seasonNumber, final Trophy trophy) {

        final League league = leagueRepository.findByUuid(leagueUuid).get();

        if (trophy.isAllowMultiple()) {
            final Player player = playerRepository.findByUuid(playerUuid);
            final Optional<TrophyForLeague> trophyForLeague =
                    trophiesForLeagueRepository.findByLeagueAndSeasonNumberAndTrophyAndPlayer(
                            league, seasonNumber, trophy, player);
            if (trophyForLeague.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Trophy of that type already exists for this player!");
            }

        } else {
            final Optional<TrophyForLeague> trophyForLeague =
                    trophiesForLeagueRepository.findByLeagueAndSeasonNumberAndTrophy(league, seasonNumber, trophy);
            if (trophyForLeague.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Trophy already existing and does not allow to add more of that type!");
            }
        }

        final Player player = playerRepository.findByUuid(playerUuid);
        final TrophyForLeague newTrophyForLeague = new TrophyForLeague(seasonNumber, player, trophy);
        newTrophyForLeague.createAudit();

        league.addTrophyForLeague(newTrophyForLeague);
        trophiesForLeagueRepository.save(newTrophyForLeague);
        LogUtil.logCreate(new TrophyDto(newTrophyForLeague));

        return newTrophyForLeague;
    }

    public void removeTrophy(
            final UUID playerUuid, final UUID leagueUuid, final int seasonNumber, final Trophy trophy) {
        final League league = leagueRepository.findByUuid(leagueUuid).get();
        final Player player = playerRepository.findByUuid(playerUuid);
        final Optional<TrophyForLeague> trophyForLeague =
                trophiesForLeagueRepository.findByLeagueAndSeasonNumberAndTrophyAndPlayer(
                        league, seasonNumber, trophy, player);
        if (trophyForLeague.isPresent()) {
            trophiesForLeagueRepository.delete(trophyForLeague.get());
            LogUtil.logCreate(new TrophyDto(trophyForLeague.get()));
        } else {
            throw new NoSuchElementException("Trophy not found!");
        }
    }

    public List<SeasonTrophies> extractTrophiesForAllSeasonsForLeague(final UUID leagueUuid) {
        final List<TrophyForLeague> allTrophiesForLeague = trophiesForLeagueRepository.findByLeagueUuid(leagueUuid);
        final LeagueDto leagueGeneralInfo = leagueService.buildGeneralInfoForLeague(leagueUuid);
        final List<Integer> seasonNumbers = leagueGeneralInfo.getSeasons()
                .stream()
                .map(SeasonDto::getSeasonNumber)
                .toList();
        final List<SeasonTrophies> leagueTrophiesPerSeason = buildSeasonTrophies(allTrophiesForLeague, seasonNumbers);
        return leagueTrophiesPerSeason;
    }

    public SeasonTrophies extractTrophiesForSingleSeasonForLeague(final UUID leagueUuid, final int seasonNumber) {
        final List<TrophyForLeague> trophiesForLeagueForSeason =
                trophiesForLeagueRepository.findByLeagueUuidAndSeasonNumber(leagueUuid, seasonNumber);
        return new SeasonTrophies(seasonNumber, trophiesForLeagueForSeason);
    }
}
