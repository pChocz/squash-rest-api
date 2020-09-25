package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"JavaDoc", "unused"})
public interface SeasonRepository extends JpaRepository<Season, Long> {

  Season findSeasonById(Long id);

  @EntityGraph(attributePaths = {
          "rounds",
          "league"
  })
  Optional<Season> findSeasonByUuid(UUID uuid);

  Season findSeasonByNumberAndLeagueId(int number, Long leagueId);

  @Query("""
          SELECT l.uuid FROM Season s
           JOIN League l ON s.league = l
              WHERE s.uuid = :seasonUuid
          """)
  UUID retrieveLeagueUuidOfSeason(@Param("seasonUuid") UUID seasonUuid);

  @Query("""
          SELECT r.id FROM Round r
          JOIN Season s ON r.season = s
            WHERE s.id = :id
            AND r.finished = TRUE
              ORDER BY r.id
          """)
  List<Long> retrieveFinishedGroupIdsBySeasonId(@Param("id") Long id);

  @Query("""
          SELECT s.id FROM Season s
              WHERE s.uuid = :seasonUuid
          """)
  Long findIdByUuid(UUID seasonUuid);

  Optional<Season> findByLeagueAndNumber(League league, int number);

}
