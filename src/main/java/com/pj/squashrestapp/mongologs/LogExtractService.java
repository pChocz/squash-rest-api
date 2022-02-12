package com.pj.squashrestapp.mongologs;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.BucketOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
class LogExtractService {

  private final LogEntryRepository logEntryRepository;
  private final MongoTemplate mongoTemplate;

  void deleteAll() {
    logEntryRepository.deleteAll();
  }

  List<LogBucket> extractLogBuckets(
          final Criteria criteria,
          final Date start,
          final Date stop,
          final int numberOfBuckets) {

    final long millisBetween = stop.getTime() - start.getTime();
    final long bucketWidthMillis = millisBetween / numberOfBuckets;

    final Date[] bucketsRangesBegins = new Date[numberOfBuckets+1];
    for (int i=0; i<numberOfBuckets+1; i++) {
      bucketsRangesBegins[i] = Date.from(start.toInstant().plus(i * bucketWidthMillis, ChronoUnit.MILLIS));
    }

    final MatchOperation bucketMatch = Aggregation.match(criteria);

    final BucketOperation bucketOperation = Aggregation
        .bucket(LogConstants.FIELD_TIMESTAMP)
        .withBoundaries((Object[]) bucketsRangesBegins)
        .andOutputCount().as(LogConstants.FIELD_AGGREGATE_SUM_COUNT);

    final Aggregation bucketAggregation = Aggregation.newAggregation(bucketMatch, bucketOperation);

    return mongoTemplate
        .aggregate(bucketAggregation, LogConstants.COLLECTION_NAME, LogBucket.class)
        .getMappedResults();
  }

  List<LogAggregateByMethod> logAggregateByMethod() {
    final MatchOperation match = Aggregation.match(Criteria.where(LogConstants.FIELD_METHOD_NAME).exists(true));

    final GroupOperation group = Aggregation.group(LogConstants.FIELD_METHOD_NAME)
        .count().as(LogConstants.FIELD_AGGREGATE_SUM_COUNT)
        .sum(LogConstants.FIELD_DURATION).as(LogConstants.FIELD_AGGREGATE_SUM_DURATION)
        .min(LogConstants.FIELD_DURATION).as(LogConstants.FIELD_AGGREGATE_MIN_DURATION)
        .max(LogConstants.FIELD_DURATION).as(LogConstants.FIELD_AGGREGATE_MAX_DURATION)
        .avg(LogConstants.FIELD_DURATION).as(LogConstants.FIELD_AGGREGATE_AVG_DURATION)
        .stdDevSamp(LogConstants.FIELD_DURATION).as(LogConstants.FIELD_AGGREGATE_DEV_DURATION)
        .sum(LogConstants.FIELD_QUERY_COUNT).as(LogConstants.FIELD_AGGREGATE_SUM_QUERY_COUNT);

    final SortOperation sort = Aggregation.sort(Sort.by(Direction.DESC, LogConstants.FIELD_AGGREGATE_AVG_DURATION));
    final Aggregation aggregation = Aggregation.newAggregation(match, group, sort);
    return mongoTemplate
        .aggregate(aggregation, LogConstants.COLLECTION_NAME, LogAggregateByMethod.class)
        .getMappedResults();
  }

  List<LogAggregateByUser> logAggregateByUser() {
    final MatchOperation match = Aggregation.match(Criteria.where(LogConstants.FIELD_USERNAME).exists(true));

    final GroupOperation group = Aggregation.group(LogConstants.FIELD_USERNAME)
        .count().as(LogConstants.FIELD_AGGREGATE_SUM_COUNT)
        .sum(LogConstants.FIELD_DURATION).as(LogConstants.FIELD_AGGREGATE_SUM_DURATION)
        .sum(LogConstants.FIELD_QUERY_COUNT).as(LogConstants.FIELD_AGGREGATE_SUM_QUERY_COUNT);

    final SortOperation sort = Aggregation.sort(Sort.by(Direction.DESC, LogConstants.FIELD_AGGREGATE_SUM_COUNT));
    final Aggregation aggregation = Aggregation.newAggregation(match, group, sort);
    return mongoTemplate
        .aggregate(aggregation, LogConstants.COLLECTION_NAME, LogAggregateByUser.class)
        .getMappedResults();
  }

  LogEntriesPaginated extractLogs(final Criteria criteria, final Pageable pageable) {
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Query query = new Query(criteria);
    log.info("query: {}", query);
    List<LogEntry> list = mongoTemplate.find(query.with(pageable), LogEntry.class, LogConstants.COLLECTION_NAME);
    Page<LogEntry> page = PageableExecutionUtils.getPage(
        list,
        pageable,
        () ->
            mongoTemplate.count(query.limit(-1).skip(-1), LogEntry.class,
                LogConstants.COLLECTION_NAME));

    stopWatch.stop();
    return new LogEntriesPaginated(page, stopWatch.getTotalTimeMillis());
  }

  LogsStats buildStatsBasedOnQuery(final Criteria criteria) {
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    final Query query = new Query(criteria);
    final Long countAll = mongoTemplate.count(query, LogEntry.class, LogConstants.COLLECTION_NAME);
    final LogsStats logsStats = new LogsStats();
    logsStats.setCount(countAll);

    if (countAll > 0) {
      final List<String> usernames = extractUniqueValuesOfStringField(query, LogConstants.FIELD_USERNAME);
      final List<String> logTypes = extractUniqueValuesOfStringField(query, LogConstants.FIELD_TYPE);
      final List<String> classNames = extractUniqueValuesOfStringField(query, LogConstants.FIELD_CLASS_NAME);
      final Pair<Date, Date> datesRange = extractMinMax(query, LogConstants.FIELD_TIMESTAMP);
      final Pair<Long, Long> durationRange = extractMinMax(query, LogConstants.FIELD_DURATION);
      final Pair<Long, Long> queryCountRange = extractMinMax(query, LogConstants.FIELD_QUERY_COUNT);

      logsStats.setUsernames(usernames);
      logsStats.setLogTypes(logTypes);
      logsStats.setClassNames(classNames);
      logsStats.setMinDateTime(datesRange.getLeft());
      logsStats.setMaxDateTime(datesRange.getRight());
      logsStats.setMinDuration(durationRange.getLeft());
      logsStats.setMaxDuration(durationRange.getRight());
      logsStats.setMinQueryCount(queryCountRange.getLeft());
      logsStats.setMaxQueryCount(queryCountRange.getRight());
    }
    stopWatch.stop();
    logsStats.setTimeTook(stopWatch.getTotalTimeMillis());

    return logsStats;
  }


  private List<String> extractUniqueValuesOfStringField(final Query query, final String fieldName) {
    DistinctIterable<String> usernames = mongoTemplate
        .getCollection(LogConstants.COLLECTION_NAME)
        .distinct(fieldName, query.getQueryObject(), String.class);

    MongoCursor<String> cursor = usernames.iterator();
    List<String> usernamesList = new ArrayList<>();
    while (cursor.hasNext()) {
      usernamesList.add(cursor.next());
    }

    return usernamesList;
  }

  private Pair extractMinMax(final Query query, final String fieldName) {
    final Pair<LogEntry, LogEntry> pair = extractMinMaxOfField(query, fieldName);
    return switch (fieldName) {
      case LogConstants.FIELD_TIMESTAMP -> Pair.of(pair.getLeft().getTimestamp(), pair.getRight().getTimestamp());
      case LogConstants.FIELD_DURATION -> Pair.of(pair.getLeft().getDuration(), pair.getRight().getDuration());
      case LogConstants.FIELD_QUERY_COUNT -> Pair.of(pair.getLeft().getQueryCount(), pair.getRight().getQueryCount());
      default -> throw new GeneralBadRequestException("NOT SUPPORTED FIELD");
    };
  }

  private Pair<LogEntry, LogEntry> extractMinMaxOfField(final Query query, final String fieldName) {
    Query queryAsc = Query.of(query);
    queryAsc.with(Sort.by(Direction.ASC, fieldName));
    queryAsc.limit(1);
    LogEntry min = mongoTemplate.findOne(queryAsc, LogEntry.class);

    Query queryDesc = Query.of(query);
    queryDesc.with(Sort.by(Direction.DESC, fieldName));
    queryDesc.limit(1);
    LogEntry max = mongoTemplate.findOne(queryDesc, LogEntry.class);

    if (min != null && max != null) {
      return Pair.of(min, max);
    }
    throw new GeneralBadRequestException("Should not happen!");
  }

}
