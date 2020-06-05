package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings({"JavaDoc", "unused"})
public interface RoundRepository extends JpaRepository<Round, Long> {

  Round findRoundById(Long id);

  @Query("""
          SELECT l.id FROM Round r
           JOIN Season s ON r.season = s
           JOIN League l ON s.league = l
              WHERE r.id = :roundId
          """)
  Long retrieveLeagueIdOfRound(@Param("roundId") Long roundId);

}
