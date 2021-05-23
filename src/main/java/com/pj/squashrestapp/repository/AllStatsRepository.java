package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.SetResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 */
public interface AllStatsRepository extends JpaRepository<SetResult, Long> {

  @Query("""
           SELECT COUNT(DISTINCT l), COUNT(DISTINCT s), COUNT(DISTINCT r), COUNT(DISTINCT m), SUM(sr.firstPlayerScore) + SUM(sr.secondPlayerScore)
             FROM SetResult sr
             INNER JOIN sr.match m
             INNER JOIN m.roundGroup rg
             INNER JOIN rg.round r
             INNER JOIN r.season s
             INNER JOIN s.league l
               WHERE sr.firstPlayerScore IS NOT NULL
                 AND sr.secondPlayerScore IS NOT NULL
          """)
  Object findLeagueRoundsMatchesRelevantCounts();


  @Query("""
           SELECT COUNT(DISTINCT m), SUM(sr.firstPlayerScore) + SUM(sr.secondPlayerScore)
             FROM AdditonalSetResult sr
             INNER JOIN sr.match m
             INNER JOIN m.league l
               WHERE sr.firstPlayerScore IS NOT NULL
                 AND sr.secondPlayerScore IS NOT NULL
          """)
  Object findAdditionalMatchesRelevantCounts();


  @Query("SELECT COUNT(DISTINCT p) FROM Player p")
  Object findPlayerCounts();

}
