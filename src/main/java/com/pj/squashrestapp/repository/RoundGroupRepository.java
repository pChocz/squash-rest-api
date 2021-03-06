package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.RoundGroup;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoundGroupRepository extends JpaRepository<RoundGroup, Long> {


  @Query("""
          SELECT rg.id FROM Match m
            INNER JOIN m.roundGroup rg
            INNER JOIN rg.round r
            INNER JOIN r.season s
            INNER JOIN s.league l
              WHERE l.uuid = :leagueUuid
              AND (m.firstPlayer.uuid = :playerUuid 
                OR m.secondPlayer.uuid = :playerUuid)
              AND r.finished = true
          """)
  List<Long> retrieveRoundGroupsIdsForPlayer(UUID leagueUuid, UUID playerUuid);

}
