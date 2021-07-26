package com.pj.squashrestapp.repository;

import java.util.List;
import java.util.UUID;

public interface BulkDeletableByLeagueUuid {

  void deleteAllByIdIn(List<Long> ids);

  List<Long> fetchIdsByLeagueUuidRaw(UUID leagueUuid);
}
