package com.pj.squashrestapp.model.dto.match;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
public class MatchesSimplePaginated {

  final int size;
  final long total;

  final int page;
  final int pages;

  final long min;
  final long max;

  final List<MatchSimpleDto> matches;

  public MatchesSimplePaginated(final Page<Long> page, final List<MatchSimpleDto> matchesDtos) {
    this.size = page.getSize();
    this.total = page.getTotalElements();
    this.page = page.getNumber();
    this.pages = page.getTotalPages();
    this.min = page.getNumber() * page.getSize() + 1;
    this.max = (page.getNumber() == page.getTotalPages() - 1)
            ? page.getTotalElements()
            : (page.getNumber() + 1) * matchesDtos.size();
    this.matches = getSortedMatches(matchesDtos);
  }

  private List<MatchSimpleDto> getSortedMatches(final Collection<MatchSimpleDto> matches) {
    return matches
            .stream()
            .sorted(Comparator
                    .comparing(MatchSimpleDto::getRoundDate)
                    .reversed())
            .collect(Collectors.toList());
  }

}
