package com.pj.squashrestapp.mongologs;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@Setter
@Getter
public class LogEntriesPaginated {

  private long timeTook;

  private int size;
  private long total;

  private int page;
  private int pages;

  private List<LogEntry> logEntries;

  LogEntriesPaginated(final Page<LogEntry> page, final long timeTook) {
    this.timeTook = timeTook;
    this.size = page.getSize();
    this.total = page.getTotalElements();
    this.page = page.getNumber();
    this.pages = page.getTotalPages();
    this.logEntries = page.getContent();
  }

}
