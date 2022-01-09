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

/**
 * Service for removal of whole league implementing bulk deletion, which drastically limits number
 * of SQL queries and improves performance.
 *
 * <p>NOTE: removal of matches from {@link MatchRepository} still takes way too much time. To be
 * checked later if it's possible to improve.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepRemovalService {

  private static final int PARTITION_SIZE = 10_000;
  private final RedisCacheService redisCacheService;

  // additional match
  private final AdditionalSetResultRepository additionalSetResultRepository;
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
    getAllRequiredRepos().forEach(repository -> deleteAllByLeagueUuid(repository, leagueUuid));
    final League league = leagueRepository.findByUuidRaw(leagueUuid);
    redisCacheService.clearAll();
    leagueRepository.delete(league);
  }

  /**
   * NOTE: order matters here, it needs to remove entities from the bottom up.
   *
   * @return list of all repos for which bulk deletion should be applied
   */
  private List<BulkDeletableByLeagueUuid> getAllRequiredRepos() {
    return List.of(
        additionalSetResultRepository,
        additionalMatchRepository,
        bonusPointRepository,
        trophiesForLeagueRepository,
        setResultRepository,
        matchRepository,
        roundGroupRepository,
        roundRepository,
        seasonRepository);
  }

  /**
   * Performs bulk-deletion of items. Partitioning is applied as number of IDs passed to
   * delete-method is limited, otherwise StackOverflow exception would occur.
   */
  private void deleteAllByLeagueUuid(
      final BulkDeletableByLeagueUuid repository, final UUID leagueUuid) {
    final List<Long> ids = repository.fetchIdsByLeagueUuidRaw(leagueUuid);
    final List<List<Long>> idsPartitions = Lists.partition(ids, PARTITION_SIZE);
    for (final List<Long> idsPartition : idsPartitions) {
      repository.deleteAllByIdIn(idsPartition);
    }
  }
}
