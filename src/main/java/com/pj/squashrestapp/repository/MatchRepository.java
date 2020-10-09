package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface MatchRepository extends JpaRepository<Match, Long> {

  String SELECT_FETCH_LEAGUE = """
          SELECT m FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
                    
          """;

  String SELECT_FETCH_LEAGUE_IDS = """
          SELECT m.id FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
                    
          """;


  @Query("""
          SELECT l.uuid FROM Match m
           JOIN RoundGroup rg ON m.roundGroup = rg
           JOIN Round r ON rg.round = r
           JOIN Season s ON r.season = s
           JOIN League l ON s.league = l
              WHERE m.uuid = :matchUuid
          """)
  UUID retrieveLeagueUuidOfMatch(UUID matchUuid);


  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  Optional<Match> findMatchByUuid(UUID uuid);


  @Query(SELECT_FETCH_LEAGUE + """
            WHERE l.uuid = :leagueUuid
              AND m.firstPlayer.uuid IN :playersUuids 
              AND m.secondPlayer.uuid IN :playersUuids
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> fetchBySeveralPlayersIdsAndLeagueId(UUID leagueUuid, UUID[] playersUuids);


  @Query(SELECT_FETCH_LEAGUE + """
            WHERE l.uuid = :leagueUuid
              AND (
                    (m.firstPlayer.uuid = :playerUuid AND m.secondPlayer.uuid IN :playersUuids)
                    OR (m.secondPlayer.uuid = :playerUuid AND m.firstPlayer.uuid IN :playersUuids)
              )
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> fetchByOnePlayerAgainstOthersAndLeagueId(UUID leagueUuid, UUID playerUuid, UUID[] playersUuids);


  @Query(SELECT_FETCH_LEAGUE_IDS + """
            WHERE l.uuid = :leagueUuid
              AND m.firstPlayer.uuid IN :playersUuids 
              AND m.secondPlayer.uuid IN :playersUuids
          """)
  Page<Long> findIdsMultiple(UUID leagueUuid, UUID[] playersUuids, Pageable pageable);


  @Query(SELECT_FETCH_LEAGUE_IDS + """
            WHERE l.uuid = :leagueUuid
              AND (m.firstPlayer.uuid = :playerUuid 
               OR m.secondPlayer.uuid = :playerUuid)
          """)
  Page<Long> findIdsSingle(UUID leagueUuid, UUID playerUuid, Pageable pageable);


  @Query(SELECT_FETCH_LEAGUE_IDS + """
            WHERE l.uuid = :leagueUuid
              AND (
                    (m.firstPlayer.uuid = :playerUuid AND m.secondPlayer.uuid IN :playersUuids)
                    OR (m.secondPlayer.uuid = :playerUuid AND m.firstPlayer.uuid IN :playersUuids)
              )
          """)
  Page<Long> findIdsSingleAgainstOthers(UUID leagueUuid, UUID playerUuid, UUID[] playersUuids, Pageable pageable);


  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> findByIdIn(List<Long> matchIds);


  @Query(SELECT_FETCH_LEAGUE + """
            WHERE l.uuid = :leagueUuid
              AND (m.firstPlayer.uuid = :playerUuid 
               OR m.secondPlayer.uuid = :playerUuid)
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> fetchForOnePlayerForLeague(UUID leagueUuid, UUID playerUuid);


  @Query("""
          select count(m), p1, p2, l from Match m
            inner join Player p1 on m.firstPlayer.id = p1.id
            inner join Player p2 on m.secondPlayer.id = p2.id
            inner join RoundGroup rg on m.roundGroup.id = rg.id
            inner join Round r on rg.round.id = r.id
            inner join Season s on r.season.id = s.id
            inner join League l on s.league.id = l.id
                    WHERE (m.firstPlayer.uuid = :playerUuid 
                     OR m.secondPlayer.uuid = :playerUuid)
                  GROUP BY p1, p2, l
                """)
  List<Object> fetchGroupedMatches(UUID playerUuid);

}
