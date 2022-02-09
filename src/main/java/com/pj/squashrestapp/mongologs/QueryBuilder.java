package com.pj.squashrestapp.mongologs;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/** */
@Slf4j
@UtilityClass
class QueryBuilder {

  Query build(
      final Optional<Date> start,
      final Optional<Date> stop,
      final Optional<Boolean> isException,
      final Optional<String> username,
      final Optional<LogType> type,
      final Optional<Long> durationMin,
      final Optional<Long> durationMax,
      final Optional<Long> queryCountMin,
      final Optional<Long> queryCountMax,
      final Optional<String> messageContains) {

    final Query query = new Query();
    applyRangeToQueryIfPresent(LogConstants.FIELD_TIMESTAMP, start, stop, query);
    applyRangeToQueryIfPresent(LogConstants.FIELD_DURATION, durationMin, durationMax, query);
    applyRangeToQueryIfPresent(LogConstants.FIELD_QUERY_COUNT, queryCountMin, queryCountMax, query);
    isException.ifPresent(s -> query.addCriteria(Criteria.where(LogConstants.FIELD_IS_EXCEPTION).is(s)));
    username.ifPresent(s -> query.addCriteria(Criteria.where(LogConstants.FIELD_USERNAME).is(s)));
    type.ifPresent(s -> query.addCriteria(Criteria.where(LogConstants.FIELD_TYPE).is(s.name())));
    messageContains.ifPresent(s -> query.addCriteria(Criteria.where(LogConstants.FIELD_MESSAGE).regex(".*" + s + ".*")));
    log.info("query: {}", query);
    return query;
  }

  private void applyRangeToQueryIfPresent(final String fieldName, final Optional min, final Optional max, final Query query) {
    if (min.isPresent() && max.isPresent()) {
      query.addCriteria(Criteria.where(fieldName).gte(min.get()).lte(max.get()));
    } else if (min.isPresent()) {
      query.addCriteria(Criteria.where(fieldName).gte(min.get()));
    } else if (max.isPresent()) {
      query.addCriteria(Criteria.where(fieldName).lte(max.get()));
    }
  }

}
