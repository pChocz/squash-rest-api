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

    Player findByUuid(UUID uuid);

    @Query("SELECT p FROM Player p")
    List<Player> findAllRaw();

    @Override
    @EntityGraph(
            attributePaths = {
                "authorities",
                "roles",
                "roles.league",
            })
    List<Player> findAll();

    @Query("SELECT p FROM Player p WHERE p.uuid IN :uuids")
    List<Player> findByUuids(@Param("uuids") UUID[] uuids);

    @Query("SELECT p FROM Player p WHERE p.id IN :ids")
    List<Player> findByIds(@Param("ids") List<Long> ids);

    @Query(
            """
          SELECT p FROM Player p
            WHERE (upper(p.username) = :usernameOrEmail OR upper(p.email) = :usernameOrEmail)
          """)
    @EntityGraph(
            attributePaths = {
                "authorities",
                "roles",
                "roles.league",
            })
    Optional<Player> fetchForAuthorizationByUsernameOrEmailUppercase(@Param("usernameOrEmail") String usernameOrEmail);

    @Query(
            """
          SELECT p FROM Player p
            WHERE (upper(p.username) = :usernameOrEmail OR upper(p.email) = :usernameOrEmail)
          """)
    @EntityGraph(
            attributePaths = {
                "authorities",
                "roles",
                "roles.league",
                "roles.players",
            })
    Optional<Player> fetchForAccountRemoval(@Param("usernameOrEmail") String usernameOrEmail);

    @Query("SELECT p FROM Player p WHERE p.uuid = :uuid")
    @EntityGraph(
            attributePaths = {
                "authorities",
                "roles",
                "roles.league",
            })
    Optional<Player> fetchForAuthorizationByUuid(@Param("uuid") UUID uuid);

    @Query(
            """
          SELECT p FROM Player p
            JOIN p.roles r
              WHERE r.league.uuid = :leagueUuid
          """)
    @EntityGraph(
            attributePaths = {
                "authorities",
                "roles",
                "roles.league",
            })
    List<Player> fetchForAuthorizationForLeague(@Param("leagueUuid") UUID leagueUuid);

    @Query(
            """
          SELECT DISTINCT p FROM Player p
            JOIN p.roles r
              WHERE r.league.uuid = :leagueUuid
          """)
    List<Player> fetchGeneralInfo(@Param("leagueUuid") UUID leagueUuid);
}
