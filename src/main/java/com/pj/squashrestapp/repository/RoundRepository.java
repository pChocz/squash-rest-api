package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"JavaDoc", "unused"})
public interface RoundRepository extends JpaRepository<Round, Long> {

  Round findRoundById(Long id);

  Optional<Round> findRoundByUuid(UUID uuid);

  @Query("""
          SELECT l.uuid FROM Round r
           JOIN Season s ON r.season = s
           JOIN League l ON s.league = l
              WHERE r.uuid = :roundUuid
          """)
  UUID retrieveLeagueUuidOfRound(@Param("roundUuid") UUID roundUuid);

  @Query("""
          SELECT r FROM Match m
           JOIN RoundGroup rg ON m.roundGroup = rg
           JOIN Round r ON rg.round = r
              WHERE m.uuid = :matchUuid
          """)
  Round findRoundByMatchUuid(@Param("matchUuid") UUID matchUuid);

  Optional<Round> findBySeasonAndNumber(Season season, int number);

  @Query("""
          SELECT r.id FROM Round r
              WHERE r.uuid = :roundUuid
          """)
  Long findIdByUuid(UUID roundUuid);

}
