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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface TrophiesForLeagueRepository extends JpaRepository<TrophyForLeague, Long>,
    SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

  @EntityGraph(attributePaths = {"player"})
  List<TrophyForLeague> findByLeagueUuid(UUID leagueUuid);

  @EntityGraph(attributePaths = {"player"})
  List<TrophyForLeague> findByLeagueUuidAndSeasonNumber(UUID leagueUuid, int seasonNumber);

  Optional<TrophyForLeague> findByLeagueAndSeasonNumberAndTrophy(League league, int seasonNumber, Trophy trophy);

  Optional<TrophyForLeague> findByLeagueAndSeasonNumberAndTrophyAndPlayer(League league, int seasonNumber, Trophy trophy, Player player);

  @Query("SELECT tfl FROM TrophyForLeague tfl WHERE tfl.player.uuid = :playerUuid")
  List<TrophyForLeague> findAllByPlayerUuid(@Param("playerUuid") UUID playerUuid);

  @Query("SELECT tfl FROM TrophyForLeague tfl WHERE tfl.player.uuid = :playerUuid AND tfl.league.uuid = :leagueUuid")
  List<TrophyForLeague> findAllByPlayerUuidAndLeagueUuid(@Param("playerUuid") UUID playerUuid, @Param("leagueUuid") UUID leagueUuid);

  @Override
  @Modifying
  @Query("DELETE FROM TrophyForLeague tfl WHERE tfl.id IN :ids")
  void deleteAllByIdIn(@Param("ids") List<Long> ids);

  @Override
  @Query("""
          SELECT tfl.id FROM TrophyForLeague tfl
            JOIN tfl.league l
              WHERE l.uuid = :leagueUuid
          """)
  List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);

  @Override
  @Query("""
          SELECT tfl.id FROM TrophyForLeague tfl
            JOIN Season s ON tfl.seasonNumber = s.number AND tfl.league = s.league
              WHERE s.uuid = :seasonUuid
          """)
  List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);

}
