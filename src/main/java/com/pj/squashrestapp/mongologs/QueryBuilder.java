package com.pj.squashrestapp.mongologs;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/** */
@Slf4j
@UtilityClass
class QueryBuilder {

  Query build(
      final Optional<LocalDateTime> start,
      final Optional<LocalDateTime> stop,
      final Optional<Boolean> isException,
      final Optional<String> username,
      final Optional<LogType> type,
      final Optional<Long> durationMin,
      final Optional<Long> durationMax,
      final Optional<Long> queryCountMin,
      final Optional<Long> queryCountMax,
      final Optional<String> messageContains) {

    final Query query = new Query();

    if (start.isPresent() && stop.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_TIMESTAMP).gte(start.get()).lte(stop.get()));
    } else if (start.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_TIMESTAMP).gte(start.get()));
    } else if (stop.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_TIMESTAMP).lte(stop.get()));
    }

    if (durationMin.isPresent() && durationMax.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_DURATION).gte(durationMin.get()).lte(durationMax.get()));
    } else if (durationMin.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_DURATION).gte(durationMin.get()));
    } else if (durationMax.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_DURATION).lte(durationMax.get()));
    }

    if (queryCountMin.isPresent() && queryCountMax.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_QUERY_COUNT).gte(queryCountMin.get()).lte(queryCountMax.get()));
    } else if (queryCountMin.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_QUERY_COUNT).gte(queryCountMin.get()));
    } else if (queryCountMax.isPresent()) {
      query.addCriteria(Criteria.where(LogConstants.FIELD_QUERY_COUNT).lte(queryCountMax.get()));
    }

    isException.ifPresent(s -> query.addCriteria(Criteria.where(LogConstants.FIELD_IS_EXCEPTION).is(s)));
    username.ifPresent(s -> query.addCriteria(Criteria.where(LogConstants.FIELD_USERNAME).is(s)));
    type.ifPresent(s -> query.addCriteria(Criteria.where(LogConstants.FIELD_TYPE).is(s.name())));
    messageContains.ifPresent(s -> query.addCriteria(Criteria.where(LogConstants.FIELD_MESSAGE).regex(".*" + s + ".*")));

    log.info("query: {}", query);

    return query;
  }

}
