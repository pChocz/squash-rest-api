package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Season;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;


/**
 *
 */
public interface SeasonRepository extends JpaRepository<Season, Long> {

  Optional<Season> findByLeagueAndNumber(League league, int number);


  @EntityGraph(attributePaths = {
          "rounds",
          "league"
  })
  Optional<Season> findSeasonByUuid(UUID uuid);


  @Query("""
          SELECT l.uuid FROM Season s
           JOIN League l ON s.league = l
              WHERE s.uuid = :seasonUuid
          """)
  UUID retrieveLeagueUuidOfSeason(UUID seasonUuid);


  @Query("""
          SELECT s.id FROM Season s
              WHERE s.uuid = :seasonUuid
          """)
  Long findIdByUuid(UUID seasonUuid);

}
