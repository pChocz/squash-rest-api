package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface LeagueRepository extends JpaRepository<League, Long> {


  @Override
  @EntityGraph(attributePaths = {
          "seasons",
          "additionalMatches",
  })
  List<League> findAll();


  @Query("SELECT l FROM League l")
  @EntityGraph(attributePaths = {
          "seasons",
  })
  List<League> findAllGeneralInfo();


  @Query("SELECT l FROM League l")
  List<League> findAllRaw();


  @EntityGraph(attributePaths = {
          "seasons",
          "trophiesForLeague",
          "rules",
          "additionalMatches",
  })
  League findByName(String name);


  Optional<League> findByUuid(UUID uuid);


  @Query("SELECT l FROM League l WHERE l.uuid = :leagueUuid")
  @EntityGraph(attributePaths = {
          "seasons.rounds.roundGroups.matches.setResults",
          "seasons.rounds.roundGroups.matches.firstPlayer",
          "seasons.rounds.roundGroups.matches.secondPlayer"
  })
  Optional<League> findByUuidForBackup(UUID leagueUuid);


  @Query("SELECT l.uuid FROM League l")
  List<UUID> findUuids();

}
