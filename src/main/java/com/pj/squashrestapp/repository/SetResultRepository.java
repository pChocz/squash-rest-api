package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Methods in this class implement 2 quite cool performance solutions:
 *
 * - Vlad Mihalcea's solution for multi-level-fetching:
 *   https://vladmihalcea.com/hibernate-facts-multi-level-fetching/
 *
 * - Durgaprasad Guduguntla's entity graph solution for ad-hoc
 *   setting attribute paths for entity graphs:
 *   https://medium.com/@gdprao/fixing-hibernate-n-1-problem-in-spring-boot-application-a99c38c5177d
 *
 * First solution makes it possible to perform multi-level fetching of:
 * - League
 *   - Season
 *     - Round
 *       - RoundGroup
 *         - Match
 *           - SetResult
 *
 * Second solution prevents additional queries to fill players fields.
 *
 * As a result we can extract entire league with a single query and
 * reconstruct it later with {@link EntityGraphBuildUtil} utility class.
 */
@SuppressWarnings({"JavaDoc", "unused"})
public interface SetResultRepository extends JpaRepository<SetResult, Long> {

  String SELECT_FETCH_LEAGUE = """
          SELECT sr FROM SetResult sr
          INNER JOIN FETCH sr.match m
          INNER JOIN FETCH m.roundGroup rg
          INNER JOIN FETCH rg.round r
          INNER JOIN FETCH r.season s
          INNER JOIN FETCH s.league l
                    
          """;

  String SELECT_FETCH_SEASON = """
          SELECT sr FROM SetResult sr
          INNER JOIN FETCH sr.match m
          INNER JOIN FETCH m.roundGroup rg
          INNER JOIN FETCH rg.round r
          INNER JOIN FETCH r.season s
                    
          """;

  String SELECT_FETCH_ROUND = """
          SELECT sr FROM SetResult sr
          INNER JOIN FETCH sr.match m
          INNER JOIN FETCH m.roundGroup rg
          INNER JOIN FETCH rg.round r
                    
          """;

  String SELECT_FETCH_ROUND_GROUP = """
          SELECT sr FROM SetResult sr
          INNER JOIN FETCH sr.match m
          INNER JOIN FETCH m.roundGroup rg
                    
          """;

  String SELECT_FETCH_MATCH = """
          SELECT sr FROM SetResult sr
          INNER JOIN FETCH sr.match m
                    
          """;


  @Query(SELECT_FETCH_LEAGUE + """
          WHERE l.id = :leagueId 
            AND m.firstPlayer.id IN :playersIds 
            AND m.secondPlayer.id IN :playersIds
          """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
  })
  List<SetResult> fetchBySeveralPlayersIdsAndLeagueId(
          @Param("leagueId") Long leagueId,
          @Param("playersIds") Long[] playersIds);


  @Query(SELECT_FETCH_LEAGUE + """
          WHERE l.id = :leagueId 
            AND (m.firstPlayer.id = :playerId 
             OR m.secondPlayer.id = :playerId)
          """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
  })
  List<SetResult> fetchByOnePlayerIdAndLeagueId(
          @Param("leagueId") Long leagueId,
          @Param("playerId") Long playerId);


  @Query(SELECT_FETCH_LEAGUE + "WHERE l.id = :leagueId")
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
          "match.roundGroup.round.season.bonusPoints"
  })
  List<SetResult> fetchByLeagueId(@Param("leagueId") Long leagueId);


  @Query(SELECT_FETCH_SEASON + "WHERE s.id = :seasonId")
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
  })
  List<SetResult> fetchBySeasonId(@Param("seasonId") Long seasonId);


  @Query(SELECT_FETCH_ROUND + "WHERE r.id = :roundId")
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
          "match.roundGroup.round.season"
  })
  List<SetResult> fetchByRoundId(@Param("roundId") Long roundId);


  @Query(SELECT_FETCH_ROUND_GROUP + "WHERE rg.id = :roundGroupId")
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
  })
  List<SetResult> fetchByRoundGroupId(@Param("roundGroupId") Long roundGroupId);


  @Query(SELECT_FETCH_MATCH + "WHERE m.id = :matchId")
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
          "match.roundGroup.round.season"
  })
  List<SetResult> fetchByMatchId(@Param("matchId") Long matchId);

}
