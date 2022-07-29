package com.pj.squashrestapp.dto.setresultshistogram;

import com.pj.squashrestapp.dto.LeagueDtoSimple;
import com.pj.squashrestapp.dto.PlayerDto;
import lombok.Getter;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Getter
public class ReadySetResultsHistogram {

    private final LeagueDtoSimple league;
    private final Set<ReadySetResultsPlayer> setResultsPlayers;
    private final Set<SetResultForHistogram> uniqueResults;

    public ReadySetResultsHistogram(final SetResultsLeagueHistogramDto dto) {
        this.league = dto.getLeague();
        this.setResultsPlayers = new TreeSet<>();
        this.uniqueResults = new TreeSet<>();
        for (final PlayerDto player :
                dto.getPlayerDtoSetResultsPlayerHistogramDtoMap().keySet()) {
            final SetResultsPlayerHistogramDto setResultsPlayerHistogramDto =
                    dto.getPlayerDtoSetResultsPlayerHistogramDtoMap().get(player);
            final TreeMap<SetResultForHistogram, Integer> resultToCountMap =
                    setResultsPlayerHistogramDto.getResultToCountMap();
            this.setResultsPlayers.add(new ReadySetResultsPlayer(player, resultToCountMap));
            this.uniqueResults.addAll(resultToCountMap.keySet());
        }
    }
}
