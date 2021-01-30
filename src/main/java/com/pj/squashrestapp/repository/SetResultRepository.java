package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

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
public interface SetResultRepository extends JpaRepository<SetResult, Long> {


  @Query("""
          SELECT sr FROM SetResult sr
            INNER JOIN FETCH sr.match m
            INNER JOIN FETCH m.roundGroup rg
            INNER JOIN FETCH rg.round r
            INNER JOIN FETCH r.season s
            INNER JOIN FETCH s.league l
              WHERE l.uuid = :leagueUuid
              """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
  })
  List<SetResult> fetchByLeagueUuid(UUID leagueUuid);


  @Query("""
          SELECT sr FROM SetResult sr
            INNER JOIN FETCH sr.match m
            INNER JOIN FETCH m.roundGroup rg
            INNER JOIN FETCH rg.round r
            INNER JOIN FETCH r.season s
              WHERE s.uuid = :seasonUuid
              """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
          "match.roundGroup.round.season.league"
  })
  List<SetResult> fetchBySeasonUuid(UUID seasonUuid);


  @Query("""
          SELECT sr FROM SetResult sr
            INNER JOIN FETCH sr.match m
            INNER JOIN FETCH m.roundGroup rg
            INNER JOIN FETCH rg.round r
              WHERE r.uuid = :roundUuid
              """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
          "match.roundGroup.round.season",
          "match.roundGroup.round.season.league"
  })
  List<SetResult> fetchByRoundUuid(UUID roundUuid);


  @Query("""
          SELECT sr FROM SetResult sr
            INNER JOIN FETCH sr.match m
            INNER JOIN FETCH m.roundGroup rg
              WHERE rg.id IN :ids
              """)
  @EntityGraph(attributePaths = {
          "match.firstPlayer",
          "match.secondPlayer",
          "match.roundGroup.round.season",
          "match.roundGroup.round.season.league"
  })
  List<SetResult> fetchByRoundGroupsIds(List<Long> ids);

}
