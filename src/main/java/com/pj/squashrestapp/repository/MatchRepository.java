package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import com.pj.squashrestapp.model.projection.MatchProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings({"JavaDoc", "unused"})
public interface MatchRepository extends JpaRepository<Match, Long> {

  String SELECT = """
          SELECT NEW com.pj.squashrestapp.model.dto.SingleSetRowDto(
              m.id AS matchId,
              p1.id AS firstPlayerId,
              p1.username AS firstPlayerName,
              p2.id AS secondPlayerId,
              p2.username AS secondPlayerName,
              rg.id AS roundGroupId,
              rg.number AS roundGroupNumber,
              r.id AS roundId,
              r.number AS roundNumber,
              r.date AS roundDate,
              s.id AS seasonId,
              s.number AS seasonNumber,
              sr.number AS setNumber,
              sr.firstPlayerScore AS firstPlayerScore,
              sr.secondPlayerScore AS secondPlayerScore)
           FROM Match m
           JOIN m.setResults sr
           JOIN RoundGroup rg ON m.roundGroup = rg
           JOIN Round r ON rg.round = r
           JOIN Season s ON r.season = s
           JOIN League l ON s.league = l
           JOIN Player p1 ON m.firstPlayer = p1
           JOIN Player p2 ON m.secondPlayer = p2
          """;


  @Query(SELECT + "WHERE p1.id = :id OR p2.id = :id")
  List<SingleSetRowDto> retrieveBySinglePlayer(@Param("id") Long id);

  @Query(SELECT + " WHERE p1.id IN :ids AND p2.id IN :ids")
  List<SingleSetRowDto> retrieveBySeveralPlayersById(@Param("ids") Long[] ids);

  @Query(SELECT + " WHERE rg.id = :roundGroupId")
  List<SingleSetRowDto> retrieveByRoundGroupId(@Param("roundGroupId") Long roundGroupId);

  @Query(SELECT + " WHERE r.id = :roundId")
  List<SingleSetRowDto> retrieveByRoundId(@Param("roundId") Long roundId);

  @Query(SELECT + " WHERE s.id = :seasonId")
  List<SingleSetRowDto> retrieveBySeasonId(@Param("seasonId") Long seasonId);


  @Query(SELECT + " WHERE l.id = :leagueId")
  List<SingleSetRowDto> retrieveByLeagueId(@Param("leagueId") Long leagueId);

  @Query(SELECT + """
          WHERE l.id = :leagueId
          AND r.finished = TRUE
          """ )
  List<SingleSetRowDto> retrieveByLeagueIdFinishedRoundsOnly(@Param("leagueId") Long leagueId);







//  @Query("SELECT DISTINCT m FROM Match m JOIN FETCH m.setResults")
//  List<MatchProjection> retrieveAllMatchesProjection();













}



