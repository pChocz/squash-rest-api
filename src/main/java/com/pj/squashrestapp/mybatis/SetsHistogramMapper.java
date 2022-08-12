package com.pj.squashrestapp.mybatis;

import com.pj.squashrestapp.dto.setresultshistogram.SetResultsHistogramDataDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

public interface SetsHistogramMapper {

    @Select(
            """
            select
                count(*) as count,
                CASE
                    WHEN greatest(t.first_player_score, t.second_player_score) = t.first_player_score THEN playerOneId
                    ELSE playerTwoId
                    END as winner_id,
                CASE
                    WHEN greatest(t.first_player_score, t.second_player_score) = t.first_player_score THEN playerTwoId
                    ELSE playerOneId
                    END as looser_id,
                greatest(t.first_player_score, t.second_player_score) as winning_result,
                least(t.first_player_score, t.second_player_score) as loosing_result
            from
                (select
                     sr.first_player_score as first_player_score,
                     sr.second_player_score as second_player_score,
                     p1.id as playerOneId,
                     p2.id as playerTwoId,
                     true as includeRow
                from set_results sr
                     join matches m on m.id = sr.match_id
                     join players p1 on m.first_player_id = p1.id
                     join players p2 on m.second_player_id = p2.id
                          
                union all
               
                select
                    sr.first_player_score as first_player_score,
                    sr.second_player_score as second_player_score,
                    p1.id as playerOneId,
                    p2.id as playerTwoId,
                    #{includeAdditional} as includeRow
                from additional_set_results sr
                    join additional_matches m on m.id = sr.match_id
                    join players p1 on m.first_player_id = p1.id
                    join players p2 on m.second_player_id = p2.id
                 ) as t
               
            where ((playerOneId = #{playerOneId} and playerTwoId = #{playerTwoId}) or (playerTwoId = #{playerOneId} and playerOneId = #{playerTwoId}))
              and t.first_player_score is not null
              and t.second_player_score is not null
              and t.includeRow = true
            group by
                winning_result,
                loosing_result,
                winner_id,
                looser_id
            """)
    List<SetResultsHistogramDataDto> getHistogramDataForTwoPlayers(
            @Param("playerOneId") Long playerOneUuid,
            @Param("playerTwoId") Long playerTwoUuid,
            @Param("includeAdditional") boolean includeAdditional);

    @Select(
            """
            select
                count(*) as count,
                CASE
                    WHEN greatest(sr.first_player_score, sr.second_player_score) = sr.first_player_score THEN p1.id
                    ELSE p2.id
                    END as winner_id,
                CASE
                    WHEN greatest(sr.first_player_score, sr.second_player_score) = sr.first_player_score THEN p2.id
                    ELSE p1.id
                    END as looser_id,
                greatest(sr.first_player_score, sr.second_player_score) as winning_result,
                least(sr.first_player_score, sr.second_player_score) as loosing_result
            from set_results sr
                     join matches m on m.id = sr.match_id
                     join round_groups rg on m.round_group_id = rg.id
                     join rounds r on rg.round_id = r.id
                     join seasons s on r.season_id = s.id
                     join leagues l on s.league_id = l.id
                     join players p1 on m.first_player_id = p1.id
                     join players p2 on m.second_player_id = p2.id
            where l.uuid = #{leagueUuid}
              and (#{seasonNumbers}::int[] is null or s.number = ANY(#{seasonNumbers}::int[]))
              and sr.first_player_score is not null
              and sr.second_player_score is not null
            group by
                winning_result,
                loosing_result,
                winner_id,
                looser_id
            """)
    List<SetResultsHistogramDataDto> getHistogramData(
            @Param("leagueUuid") UUID leagueUuid, @Param("seasonNumbers") int[] seasonNumbers);
}
