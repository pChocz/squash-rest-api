package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Player;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {

  Player findByUsername(String username);

  List<Player> findByUsernameIn(List<String> username);

  @Query("SELECT p FROM Player p WHERE p.uuid IN :uuids")
  List<Player> findByUuids(UUID[] uuids);

  Player findByUuid(UUID uuid);

  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("SELECT p FROM Player p WHERE (upper(p.username) = :usernameOrEmail OR upper(p.email) = :usernameOrEmail)")
  Optional<Player> fetchForAuthorizationByUsernameOrEmailUppercase(String usernameOrEmail);


  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("SELECT p FROM Player p WHERE p.id = :playerId")
  Optional<Player> fetchForAuthorizationById(Long playerId);


  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("SELECT p FROM Player p WHERE p.uuid = :uuid")
  Optional<Player> fetchForAuthorizationByUuid(UUID uuid);


  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  List<Player> findAll();


  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("""
          SELECT p FROM Player p
            JOIN p.roles r
              WHERE r.league.uuid = :leagueUuid
          """)
  List<Player> fetchForAuthorizationForLeague(UUID leagueUuid);

  @Query("""
          SELECT DISTINCT p FROM Player p
            JOIN p.roles r
              WHERE r.league.uuid = :leagueUuid
          """)
  List<Player> fetchGeneralInfoSorted(UUID leagueUuid, Sort sort);

}
