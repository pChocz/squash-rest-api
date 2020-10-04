package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.SetResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeagueRepository extends JpaRepository<League, Long> {

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
          SELECT l.id FROM League l
              WHERE l.uuid = :leagueUuid
          """)
  Long findIdByUuid(UUID leagueUuid);

  @Query("""
          SELECT l.name FROM League l
              WHERE l.uuid = :leagueUuid
          """)
  String findNameByUuid(UUID leagueUuid);

}
