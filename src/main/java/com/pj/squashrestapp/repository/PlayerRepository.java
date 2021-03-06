package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Player;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {


  Player findByUsername(String username);


  List<Player> findByUsernameIn(List<String> username);


  Player findByUuid(UUID uuid);


  @Override
  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  List<Player> findAll();


  @Query("""
          SELECT p FROM Player p 
            WHERE p.uuid IN :uuids
            """)
  List<Player> findByUuids(UUID[] uuids);


  @Query("""
          SELECT p FROM Player p 
            WHERE (upper(p.username) = :usernameOrEmail 
              OR upper(p.email) = :usernameOrEmail)
              """)
  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  Optional<Player> fetchForAuthorizationByUsernameOrEmailUppercase(String usernameOrEmail);


  @Query("""
          SELECT p FROM Player p 
            WHERE p.uuid = :uuid
            """)
  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  Optional<Player> fetchForAuthorizationByUuid(UUID uuid);


  @Query("""
          SELECT p FROM Player p
            JOIN p.roles r
              WHERE r.league.uuid = :leagueUuid
              """)
  @EntityGraph(attributePaths = {
          "authorities",
          "roles",
          "roles.league",
  })
  List<Player> fetchForAuthorizationForLeague(UUID leagueUuid);


  @Query("""
          SELECT DISTINCT p FROM Player p
            JOIN p.roles r
              WHERE r.league.uuid = :leagueUuid
              """)
  List<Player> fetchGeneralInfoSorted(UUID leagueUuid, Sort sort);

}
