package com.pj.squashrestapp.service;

import com.google.common.collect.Lists;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.AdditionalSetResultRepository;
import com.pj.squashrestapp.repository.BonusPointRepository;
import com.pj.squashrestapp.repository.BulkDeletableByLeagueUuid;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.RoundGroupRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.TrophiesForLeagueRepository;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepRemovalService {

  private static final int PARTITION_SIZE = 10_000;

  // additional match
  private final AdditionalSetResultRepository additonalSetResultRepository;
  private final AdditionalMatchRepository additionalMatchRepository;

  // round match
  private final SetResultRepository setResultRepository;
  private final MatchRepository matchRepository;
  private final RoundGroupRepository roundGroupRepository;
  private final RoundRepository roundRepository;
  private final BonusPointRepository bonusPointRepository;
  private final SeasonRepository seasonRepository;

  // trophies
  private final TrophiesForLeagueRepository trophiesForLeagueRepository;

  // league
  private final LeagueRepository leagueRepository;

  @Transactional
  public void deepRemoveLeague(final UUID leagueUuid) {

    // NOTE: order matters here, it needs to remove entities from the bottom up.
    final List<BulkDeletableByLeagueUuid> repos =
        List.of(
            // additional matches
            additonalSetResultRepository,
            additionalMatchRepository,
            // bonus points and trophies
            bonusPointRepository,
            trophiesForLeagueRepository,
            // round matches
            setResultRepository,
            matchRepository,
            roundGroupRepository,
            roundRepository,
            seasonRepository);

    repos.forEach(repository -> deleteAllByLeagueUuid(repository, leagueUuid));

    // league
    final League league = leagueRepository.findByUuidRaw(leagueUuid);
    leagueRepository.delete(league);
  }

  private void deleteAllByLeagueUuid(
      final BulkDeletableByLeagueUuid repository, final UUID leagueUuid) {
    final List<Long> ids = repository.fetchIdsByLeagueUuidRaw(leagueUuid);
    final List<List<Long>> idsPartitions = Lists.partition(ids, PARTITION_SIZE);
    for (final List<Long> idsPartition : idsPartitions) {
      repository.deleteAllByIdIn(idsPartition);
    }
  }
}
