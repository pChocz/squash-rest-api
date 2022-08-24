package com.pj.squashrestapp.mybatis;

import com.pj.squashrestapp.dto.matchresultsdistribution.MatchResultDistributionDataDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

public interface MatchDistributionMapper {

    @Select(
            """
            select
                count(*) as count,
                CASE
                    WHEN tt.player_one_games > tt.player_two_games THEN tt.player_one_id
                    ELSE tt.player_two_id
                    END as winner_id,
                CASE
                    WHEN tt.player_one_games < tt.player_two_games THEN tt.player_one_id
                    ELSE tt.player_two_id
                    END as looser_id,
                greatest(tt.player_one_games, tt.player_two_games) as games_won,
                least(tt.player_one_games, tt.player_two_games) as games_lost
            from
                (select
                    t.match_id,
                    t.match_format_type,
                    t.player_one_id,
                    t.player_two_id,
                    count(CASE WHEN t.player_one_result > t.player_two_result THEN 1 END) as player_one_games,
                    count(CASE WHEN t.player_one_result < t.player_two_result THEN 1 END) as player_two_games,
                    count(*) as games
                from
                    (select
                        m.id as match_id,
                        m.match_format_type as match_format_type,
                        p1.id as player_one_id,
                        p2.id as player_two_id,
                        sr.first_player_score as player_one_result,
                        sr.second_player_score as player_two_result,
                        true as includeRow
                    from set_results sr
                        join matches m on sr.match_id = m.id
                        join round_groups rg on rg.id = m.round_group_id
                        join rounds r on r.id = rg.round_id
                        join seasons s on s.id = r.season_id
                        join leagues l on l.id = s.league_id
                        join players p1 on m.first_player_id = p1.id
                        join players p2 on m.second_player_id = p2.id
                    where l.uuid = #{leagueUuid}
                      and (#{seasonNumbers}::int[] is null or s.number = ANY(#{seasonNumbers}::int[]))
                      and sr.first_player_score is not null
                      and sr.second_player_score is not null
                      
                    union all
                    
                    select
                        m.id as match_id,
                        m.match_format_type as match_format_type,
                        p1.id as player_one_id,
                        p2.id as player_two_id,
                        sr.first_player_score as player_one_result,
                        sr.second_player_score as player_two_result,
                        #{includeAdditional} as includeRow
                    from additional_set_results sr
                        join additional_matches m on sr.match_id = m.id
                        join leagues l on l.id = m.league_id
                        join players p1 on m.first_player_id = p1.id
                        join players p2 on m.second_player_id = p2.id
                    where l.uuid = #{leagueUuid}
                      and (#{seasonNumbers}::int[] is null or m.season_number = ANY(#{seasonNumbers}::int[]))
                      and sr.first_player_score is not null
                      and sr.second_player_score is not null
                      
                    ) as t
                where t.includeRow = true
                group by
                    t.match_id,
                    t.match_format_type,
                    t.player_one_id,
                    t.player_two_id) as tt
            group by
                winner_id,
                looser_id,
                games_won,
                games_lost
            """)
    List<MatchResultDistributionDataDto> getDistributionData(
            @Param("leagueUuid") UUID leagueUuid,
            @Param("seasonNumbers") int[] seasonNumbers,
            @Param("includeAdditional") boolean includeAdditional);
}
