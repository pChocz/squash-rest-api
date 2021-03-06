package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.dto.Trophy;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.TrophyForLeague;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 */
public interface TrophiesForLeagueRepository extends JpaRepository<TrophyForLeague, Long> {

  @EntityGraph(attributePaths = {"player"})
  List<TrophyForLeague> findByLeagueUuid(UUID leagueUuid);

  Optional<TrophyForLeague> findByLeagueAndSeasonNumberAndTrophy(League league, int seasonNumber, Trophy trophy);

  Optional<TrophyForLeague> findByLeagueAndSeasonNumberAndTrophyAndPlayer(League league, int seasonNumber, Trophy trophy, Player player);

  @Query("""
          SELECT tfl FROM TrophyForLeague tfl
            WHERE tfl.player.uuid = :playerUuid
            """)
  List<TrophyForLeague> findAllByPlayerUuid(UUID playerUuid);

}
