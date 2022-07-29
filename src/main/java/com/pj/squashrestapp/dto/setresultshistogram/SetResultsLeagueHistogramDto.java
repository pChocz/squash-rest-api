package com.pj.squashrestapp.dto.setresultshistogram;

import com.pj.squashrestapp.dto.LeagueDtoSimple;
import com.pj.squashrestapp.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetResultsLeagueHistogramDto {

    private LeagueDtoSimple league;
    private Map<PlayerDto, SetResultsPlayerHistogramDto> playerDtoSetResultsPlayerHistogramDtoMap;

    public void createOrUpdate(final PlayerDto player, final int firstResult, final int secondResult, final int count) {
        playerDtoSetResultsPlayerHistogramDtoMap.computeIfAbsent(
                player, f -> new SetResultsPlayerHistogramDto(new TreeMap<>()));
        playerDtoSetResultsPlayerHistogramDtoMap
                .get(player)
                .getResultToCountMap()
                .merge(new SetResultForHistogram(firstResult, secondResult), count, Integer::sum);
    }
}
