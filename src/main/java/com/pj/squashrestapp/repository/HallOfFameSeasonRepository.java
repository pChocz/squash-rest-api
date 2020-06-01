package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.HallOfFameSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings({"JavaDoc", "unused"})
public interface HallOfFameSeasonRepository extends JpaRepository<HallOfFameSeason, Long> {

  @Query("SELECT h FROM HallOfFameSeason h WHERE h.league.id = :leagueId")
  List<HallOfFameSeason> retrieveByLeagueId(@Param("leagueId") Long leagueId);

}
