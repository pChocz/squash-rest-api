package com.pj.squashrestapp.mybatis;

import com.pj.squashrestapp.dto.matchresultsdistribution.MatchResultDistributionDataDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface MatchDistributionMapper {

    List<MatchResultDistributionDataDto> getDistributionDataForLeague(
            @Param("leagueUuid") UUID leagueUuid,
            @Param("seasonNumbers") int[] seasonNumbers,
            @Param("includeAdditional") boolean includeAdditional);
}
