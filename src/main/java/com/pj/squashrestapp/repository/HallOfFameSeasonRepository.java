package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings({"JavaDoc", "unused"})
public interface HallOfFameSeasonRepository extends JpaRepository<HallOfFameSeason, Long> {


  List<HallOfFameSeason> findByLeague(
          @Param("league") League league);


  HallOfFameSeason findByLeagueAndSeasonNumber(
          @Param("leagueId") League league,
          @Param("seasonNumber") int seasonNumber);


}
