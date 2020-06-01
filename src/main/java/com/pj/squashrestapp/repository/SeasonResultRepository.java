package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.SeasonResult;
import com.pj.squashrestapp.model.dto.SeasonResultDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface SeasonResultRepository extends JpaRepository<SeasonResult, Long> {

//  @Query("""
//          SELECT DISTINCT sr FROM SeasonResult sr
//          JOIN sr.pointsPerRound ppr
//          WHERE sr.season.id = :id
//          """)
//  List<SeasonResult> retrieveSeasonResultsBySeasonId(@Param("id") Long id);
//
//  @Query("""
//          SELECT NEW com.pj.squashrestapp.model.dto.SeasonResultDto(
//              sr.id AS id,
//              sr.season.id AS seasonId,
//              sr.player.id AS playerId,
//              sr.pointsPerRound AS pointsPerRound,
//              sr.bonusPoints AS bonusPoints)
//           FROM SeasonResult sr
//           JOIN sr.pointsPerRound ppr
//           WHERE sr.season.id = :id
//           """)
//  List<SeasonResultDto> retrieveSeasonResultsDtosBySeasonId(@Param("id") Long id);


}
