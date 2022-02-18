package com.pj.squashrestapp.mongologs;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** */
@Slf4j
@UtilityClass
class CriteriaForQueryBuilder {

  Criteria build(
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

    final List<Criteria> criteria = new ArrayList<>();
    criteria.add(addCriteriaRangeIfPresent(LogConstants.FIELD_TIMESTAMP, start, stop));
    criteria.add(addCriteriaRangeIfPresent(LogConstants.FIELD_DURATION, durationMin, durationMax));
    criteria.add(addCriteriaRangeIfPresent(LogConstants.FIELD_QUERY_COUNT, queryCountMin, queryCountMax));
    isException.ifPresent(s -> criteria.add(Criteria.where(LogConstants.FIELD_IS_EXCEPTION).is(s)));
    username.ifPresent(s -> criteria.add(Criteria.where(LogConstants.FIELD_USERNAME).is(s)));
    type.ifPresent(s -> criteria.add(Criteria.where(LogConstants.FIELD_TYPE).is(s.name())));
    messageContains.ifPresent(s -> criteria.add(Criteria.where(LogConstants.FIELD_MESSAGE).regex(".*" + s.replace("*", ".*") + ".*", "i")));

    List<Criteria> nonEmptyCriteria = criteria
            .stream()
            .filter(c -> !c.equals(new Criteria()))
            .collect(Collectors.toList());

    if (nonEmptyCriteria.isEmpty()) {
      return new Criteria();
    } else {
      return new Criteria().andOperator(nonEmptyCriteria);
    }
  }

  private Criteria addCriteriaRangeIfPresent(final String fieldName, final Optional min, final Optional max) {
    Criteria criteria = new Criteria();
    if (min.isPresent() && max.isPresent()) {
      criteria = Criteria.where(fieldName).gte(min.get()).lte(max.get());
    } else if (min.isPresent()) {
      criteria = Criteria.where(fieldName).gte(min.get());
    } else if (max.isPresent()) {
      criteria = Criteria.where(fieldName).lte(max.get());
    }
    return criteria;
  }

}
