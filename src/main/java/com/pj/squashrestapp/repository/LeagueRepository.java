package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Blob;

public interface LeagueRepository extends JpaRepository<League, Long> {

  @Query("SELECT l.logo FROM League l WHERE l.id = :id")
  Blob retrieveLogoForLeagueId(@Param("id") Long id);

  League findRawById(Long id);


  //  @EntityGraph(attributePaths = {
//          "location",
//          "location.country",
//          "location.country.region"})
//  @EntityGraph(attributePaths = {
//          "seasons",
//          "seasons.rounds",
//          "seasons.rounds.roundGroups",
//          "seasons.rounds.roundGroups.matches",
//          "seasons.rounds.roundGroups.matches.firstPlayer",
//          "seasons.rounds.roundGroups.matches.secondPlayer",
//          "seasons.rounds.roundGroups.matches.setResults"
//  })
  League findWithEntityGraphById(Long id);





//  League findById(Long id);



}
