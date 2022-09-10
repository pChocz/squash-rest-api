package com.pj.squashrestapp.mybatis;

import com.pj.squashrestapp.dto.setresultshistogram.SetResultsHistogramDataDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface GameDistributionMapper {

    List<SetResultsHistogramDataDto> getDistributionDataForTwoPlayers(
            @Param("playerOneId") Long playerOneUuid,
            @Param("playerTwoId") Long playerTwoUuid,
            @Param("includeAdditional") boolean includeAdditional);

    List<SetResultsHistogramDataDto> getDistributionDataForLeague(
            @Param("leagueUuid") UUID leagueUuid,
            @Param("seasonNumbers") int[] seasonNumbers,
            @Param("includeAdditional") boolean includeAdditional
    );
}
