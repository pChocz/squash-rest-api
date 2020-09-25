package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"JavaDoc", "unused"})
public interface MatchRepository extends JpaRepository<Match, Long> {

  String SELECT_FETCH_LEAGUE = """
          SELECT m FROM Match m
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
  UUID retrieveLeagueUuidOfMatch(@Param("matchUuid") UUID matchUuid);


  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  Match findMatchById(Long id);


  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  Optional<Match> findMatchByUuid(UUID uuid);


  @Query(SELECT_FETCH_LEAGUE + """
            WHERE l.uuid = :leagueUuid
              AND m.firstPlayer.id IN :playersIds 
              AND m.secondPlayer.id IN :playersIds
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> fetchBySeveralPlayersIdsAndLeagueId(
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playersIds") Long[] playersIds);


  @Query(SELECT_FETCH_LEAGUE + """
            WHERE l.uuid = :leagueUuid
              AND m.firstPlayer.id IN :playersIds 
              AND m.secondPlayer.id IN :playersIds
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  Page<Match> fetchPageBySeveralPlayersIdsAndLeagueId(
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playersIds") Long[] playersIds,
          Pageable pageable);




  @Query("""
          SELECT m.id FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
            WHERE l.uuid = :leagueUuid
              AND m.firstPlayer.id IN :playersIds 
              AND m.secondPlayer.id IN :playersIds
          """)
  Page<Long> findIdsMultiple(
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playersIds") Long[] playersIds,
          Pageable pageable);


  @Query("""
          SELECT m.id FROM Match m
          INNER JOIN m.roundGroup rg
          INNER JOIN rg.round r
          INNER JOIN r.season s
          INNER JOIN s.league l
            WHERE l.uuid = :leagueUuid
              AND (m.firstPlayer.id = :playerId 
               OR m.secondPlayer.id = :playerId)
          """)
  Page<Long> findIdsSingle(
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playerId") Long playerId,
          Pageable pageable);

  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> findByIdIn(List<Long> matchIds);


  @Query(SELECT_FETCH_LEAGUE + """
            WHERE l.uuid = :leagueUuid
              AND (m.firstPlayer.id = :playerId 
               OR m.secondPlayer.id = :playerId)
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "roundGroup.round.season"
  })
  List<Match> fetchByOnePlayerIdAndLeagueId(
          @Param("leagueUuid") UUID leagueUuid,
          @Param("playerId") Long playerId);

}

//  @Query(SELECT_FETCH_LEAGUE + """
//          WHERE l.uuid = :leagueUuid
//            AND (m.firstPlayer.id = :playerId
//             OR m.secondPlayer.id = :playerId)
//          """)
//  @EntityGraph(attributePaths = {
//          "match.firstPlayer",
//          "match.secondPlayer",
//  })
//  List<SetResult> fetchByOnePlayerIdAndLeagueId(
//          @Param("leagueUuid") UUID leagueUuid,
//          @Param("playerId") Long playerId);
