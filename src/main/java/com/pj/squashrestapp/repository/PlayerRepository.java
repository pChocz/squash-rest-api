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
@SuppressWarnings({"unused", "JavaDoc"})
public interface PlayerRepository extends JpaRepository<Player, Long> {

  Player findByUsername(@Param("username") String username);

  List<Player> findByUsernameIn(List<String> username);

  @Query("SELECT p FROM Player p WHERE p.id IN :ids")
  List<Player> findByIds(@Param("ids") Long[] ids);

  Player findByUuid(UUID uuid);

  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("SELECT p FROM Player p WHERE (upper(p.username) = :usernameOrEmail OR upper(p.email) = :usernameOrEmail)")
  Optional<Player> fetchForAuthorizationByUsernameOrEmailUppercase(@Param("usernameOrEmail") String usernameOrEmail);


  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("SELECT p FROM Player p WHERE p.id = :playerId")
  Optional<Player> fetchForAuthorizationById(@Param("playerId") Long playerId);


  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("SELECT p FROM Player p WHERE p.uuid = :uuid")
  Optional<Player> fetchForAuthorizationByUuid(@Param("uuid") UUID uuid);


  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("SELECT p FROM Player p")
  List<Player> fetchForAuthorizationAll();


  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  @Query("""
          SELECT p FROM Player p
            JOIN p.roles r
              WHERE r.league.id = :leagueId
          """)
  List<Player> fetchForAuthorizationForLeague(@Param("leagueId") Long leagueId);

  @Query("""
          SELECT DISTINCT p FROM Player p
            JOIN p.roles r
              WHERE r.league.uuid = :leagueUuid
          """)
  List<Player> fetchGeneralInfoSorted(@Param("leagueUuid") UUID leagueUuid, Sort sort);

}
