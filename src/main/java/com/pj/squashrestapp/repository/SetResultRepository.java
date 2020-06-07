package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.SeasonResult;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.util.EntityGraphReconstruct;
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
 * reconstruct it later with {@link EntityGraphReconstruct} utility class.
 */
@SuppressWarnings({"JavaDoc", "unused"})
public interface SetResultRepository extends JpaRepository<SetResult, Long> {

  @Query("""
          SELECT sr FROM SetResult sr
          INNER JOIN FETCH sr.match m
          INNER JOIN FETCH m.roundGroup rg
          INNER JOIN FETCH rg.round r
          INNER JOIN FETCH r.season s
          INNER JOIN FETCH s.league l
            WHERE l.id = :leagueId
          """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
  })
  List<SetResult> fetchByLeagueId(@Param("leagueId") Long leagueId);

  @Query("""
          SELECT sr FROM SetResult sr
          INNER JOIN FETCH sr.match m
          INNER JOIN FETCH m.roundGroup rg
          INNER JOIN FETCH rg.round r
          INNER JOIN FETCH r.season s
            WHERE s.id = :seasonId
          """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
  })
  List<SetResult> fetchBySeasonId(@Param("seasonId") Long seasonId);

  @Query("""
          SELECT sr FROM SetResult sr
          INNER JOIN FETCH sr.match m
          INNER JOIN FETCH m.roundGroup rg
          INNER JOIN FETCH rg.round r
            WHERE r.id = :roundId
          """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
  })
  List<SetResult> fetchByRoundId(@Param("roundId") Long roundId);



}
