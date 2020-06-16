package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Match;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings({"JavaDoc", "unused"})
public interface MatchRepository extends JpaRepository<Match, Long> {

  @Query("""
          SELECT l.id FROM Match m
           JOIN RoundGroup rg ON m.roundGroup = rg
           JOIN Round r ON rg.round = r
           JOIN Season s ON r.season = s
           JOIN League l ON s.league = l
              WHERE m.id = :matchId
          """)
  Long retrieveLeagueIdOfMatch(@Param("matchId") Long matchId);

  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  Match findMatchById(Long id);

}



