package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 */
public interface HallOfFameSeasonRepository extends JpaRepository<HallOfFameSeason, Long> {

  List<HallOfFameSeason> findByLeague(League league);

}
