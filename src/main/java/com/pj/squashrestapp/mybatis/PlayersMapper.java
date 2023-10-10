package com.pj.squashrestapp.mybatis;

import com.pj.squashrestapp.dto.PlayerDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface PlayersMapper {

    List<PlayerDto> getAllPlayersForLeague(@Param("leagueUuid") UUID leagueUuid);
}
