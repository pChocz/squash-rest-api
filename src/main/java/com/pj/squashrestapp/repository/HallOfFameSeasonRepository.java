package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 */
public interface HallOfFameSeasonRepository extends JpaRepository<HallOfFameSeason, Long> {


  List<HallOfFameSeason> findByLeague(League league);


  @Query("""
          SELECT hof FROM HallOfFameSeason hof
            WHERE hof.league1stPlace LIKE :playerName
              OR hof.league2ndPlace LIKE :playerName
              OR hof.league3rdPlace LIKE :playerName
              OR hof.cup1stPlace LIKE :playerName
              OR hof.cup2ndPlace LIKE :playerName
              OR hof.cup3rdPlace LIKE :playerName
              OR hof.superCupWinner LIKE :playerName
              OR hof.pretendersCupWinner LIKE :playerName
              OR hof.coviders LIKE CONCAT('%',:playerName,'%')
              OR hof.allRoundsAttendees LIKE CONCAT('%',:playerName,'%')
            ORDER BY hof.seasonNumber DESC
            """)
  @EntityGraph(attributePaths = {
          "league",
  })
  List<HallOfFameSeason> findAllByPlayerName(String playerName);

}
