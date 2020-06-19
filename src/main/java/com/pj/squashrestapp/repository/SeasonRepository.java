package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings({"JavaDoc", "unused"})
public interface SeasonRepository extends JpaRepository<Season, Long> {

  Season findSeasonById(Long id);

  Season findSeasonByNumberAndLeagueId(int number, Long leagueId);

  @Query("""
          SELECT l.id FROM Season s
           JOIN League l ON s.league = l
              WHERE s.id = :seasonId
          """)
  Long retrieveLeagueIdOfSeason(@Param("seasonId") Long seasonId);

  @Query("""
          SELECT r.id FROM Round r
          JOIN Season s ON r.season = s
            WHERE s.id = :id
            AND r.finished = TRUE
              ORDER BY r.id
          """)
  List<Long> retrieveFinishedGroupIdsBySeasonId(@Param("id") Long id);

}
