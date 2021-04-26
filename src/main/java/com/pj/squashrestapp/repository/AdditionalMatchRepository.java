package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public interface AdditionalMatchRepository extends JpaRepository<AdditionalMatch, Long> {

  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults"
  })
  Optional<AdditionalMatch> findByUuid(UUID uuid);


  @Query("""
          SELECT m FROM AdditionalMatch m
            INNER JOIN m.firstPlayer p1
            INNER JOIN m.secondPlayer p2
              WHERE (p1 = :player 
                  OR p2 = :player)
                  AND m.league = :league
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "league"
  })
  List<AdditionalMatch> fetchForSinglePlayerForLeague(Player player, League league);


  @Query("""
          SELECT m FROM AdditionalMatch m
            INNER JOIN m.firstPlayer p1
            INNER JOIN m.secondPlayer p2
              WHERE (p1.uuid IN :playersUuids 
                 AND p2.uuid IN :playersUuids)
                 AND m.league = :league
          """)
  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "league"
  })
  List<AdditionalMatch> fetchForMultiplePlayersForLeague(UUID[] playersUuids, League league);


  @EntityGraph(attributePaths = {
          "firstPlayer",
          "secondPlayer",
          "setResults",
          "league"
  })
  List<AdditionalMatch> findAllByLeagueOrderByDateDesc(League league);

}
