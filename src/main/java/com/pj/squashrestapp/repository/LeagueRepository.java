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
  })
  List<League> findAll();


  League findByName(String name);


  Optional<League> findByUuid(UUID uuid);


  @EntityGraph(attributePaths = {
          "seasons.rounds.roundGroups.matches.setResults",
          "seasons.rounds.roundGroups.matches.firstPlayer",
          "seasons.rounds.roundGroups.matches.secondPlayer",
  })
  @Query("SELECT l FROM League l WHERE l.uuid = :leagueUuid")
  Optional<League> findByUuidForBackup(UUID leagueUuid);


  @Query("SELECT l.uuid FROM League l")
  List<UUID> findUuids();


  @Query("""
          SELECT l.name FROM League l
              WHERE l.uuid = :leagueUuid
          """)
  String findNameByUuid(UUID leagueUuid);

}
