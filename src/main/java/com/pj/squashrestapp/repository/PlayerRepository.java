package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.PlayerAuthDto;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 */
@SuppressWarnings({"unused", "JavaDoc"})
public interface PlayerRepository extends JpaRepository<Player, Long> {


  Player findByUsernameOrEmail(String username, String email);

  Player findByUsername(String username);


  @Query("SELECT p FROM Player p WHERE p.id IN :ids")
  List<Player> findByIds(@Param("ids") Long[] ids);


  @Query("""
          SELECT NEW com.pj.squashrestapp.model.dto.PlayerDetailedDto(  
            p.id AS id,
            p.username AS name,
            p.email AS email)
          FROM Player p
            JOIN p.roles r
              WHERE r.league.id = :leagueId
              AND r.leagueRole = :leagueRole
          """)
  List<PlayerDetailedDto> findByLeague(@Param("leagueId") Long leagueId,
                                       @Param("leagueRole") LeagueRole leagueRole);

  @Query("""
          SELECT NEW com.pj.squashrestapp.model.dto.PlayerAuthDto(
            p.username AS username,
            p.password AS password,
            a.type AS authorityType,
            r.leagueRole AS role,
            l.name AS leagueName)
          FROM Player p
            JOIN p.authorities a
            JOIN p.roles r
            JOIN r.league l
              WHERE (p.username = :usernameOrEmail
                     OR p.email = :usernameOrEmail)
          """)
  List<PlayerAuthDto> getAuthorizationData(@Param("usernameOrEmail") String usernameOrEmail);


  @Query("""
          SELECT r.leagueRole FROM Player p
            JOIN p.roles r
              WHERE p.id = :id
              AND r.league.id = :leagueId
          """)
  List<LeagueRole> findRolesForUserByLeague(@Param("id") Long id,
                                            @Param("leagueId") Long leagueId);


  @Query("""
          SELECT r.leagueRole FROM Player p
            JOIN p.roles r
              WHERE p.username = :username
          """)
  List<LeagueRole> findAllRolesForUsername(@Param("username") String username);


  @Query("""
          SELECT a.type FROM Player p
            JOIN p.authorities a
              WHERE p.username = :username
          """)
  List<AuthorityType> findAuthoritiesForUsername(@Param("username") String username);


  @Query("""
          SELECT a.type FROM Player p
            JOIN p.authorities a
              WHERE p.id = :id
          """)
  List<AuthorityType> findAuthoritiesForUser(@Param("id") Long id);


  Player getPlayerByUsername(String username);

}
