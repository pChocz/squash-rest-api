package com.pj.squashrestapp.dto.match;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.data.domain.Page;

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

  final List<MatchDto> matches;

  public MatchesSimplePaginated(final Page<Long> page, final List<MatchDto> matchesDtos) {
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

  private List<MatchDto> getSortedMatches(final Collection<MatchDto> matches) {
    return matches
            .stream()
            .sorted(Comparator
                    .comparing(MatchDto::getDate)
                    .reversed())
            .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "matches: " + matches.size() + " | page: " + page + "/" + pages;
  }

}
