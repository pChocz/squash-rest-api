package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.dto.setresultshistogram.ReadySetResultsHistogram;
import com.pj.squashrestapp.dto.setresultshistogram.SetResultsHistogramDataDto;
import com.pj.squashrestapp.dto.setresultshistogram.SetResultsLeagueHistogramDto;
import com.pj.squashrestapp.mybatis.SetsHistogramMapper;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class SetResultsHistogramService {

    private final SetsHistogramMapper setsHistogramMapper;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;


    public ReadySetResultsHistogram createHistogram(final UUID leagueUuid, final int[] seasonNumbers) {
        final League league = leagueRepository.findByUuidWithSeasons(leagueUuid).orElseThrow();
        final List<SetResultsHistogramDataDto> results = setsHistogramMapper.getHistogramData(leagueUuid, seasonNumbers);
        final Map<Long, PlayerDto> players = playerRepository.findByIds(getPlayersIds(results))
                .stream()
                .collect(Collectors.toMap(Player::getId, PlayerDto::new));

        final ReadySetResultsHistogram histogram = buildHistogram(results, players);
        histogram.setLeague(new LeagueDto(league));

        return histogram;
    }

    public ReadySetResultsHistogram buildHistogram(final List<SetResultsHistogramDataDto> results, final Map<Long, PlayerDto> players) {
        final SetResultsLeagueHistogramDto setResultsLeagueHistogramDto = new SetResultsLeagueHistogramDto();
        setResultsLeagueHistogramDto.setPlayerDtoSetResultsPlayerHistogramDtoMap(new LinkedHashMap<>());

        for (SetResultsHistogramDataDto setResultsHistogramDataDto : results) {
            final PlayerDto winner = players.get(setResultsHistogramDataDto.getWinnerId());
            final PlayerDto looser = players.get(setResultsHistogramDataDto.getLooserId());

            setResultsLeagueHistogramDto.createOrUpdate(
                    winner,
                    setResultsHistogramDataDto.getWinningResult(),
                    setResultsHistogramDataDto.getLoosingResult(),
                    setResultsHistogramDataDto.getCount());

            setResultsLeagueHistogramDto.createOrUpdate(
                    looser,
                    setResultsHistogramDataDto.getLoosingResult(),
                    setResultsHistogramDataDto.getWinningResult(),
                    setResultsHistogramDataDto.getCount());
        }

        final ReadySetResultsHistogram histogram = new ReadySetResultsHistogram(setResultsLeagueHistogramDto);
        return histogram;
    }

    private List<Long> getPlayersIds(final List<SetResultsHistogramDataDto> results) {
        return Stream.concat(
                results.stream().map(SetResultsHistogramDataDto::getWinnerId).distinct(),
                results.stream().map(SetResultsHistogramDataDto::getLooserId).distinct()
        ).distinct().toList();
    }
}
