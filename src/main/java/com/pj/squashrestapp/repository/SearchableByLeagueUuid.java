package com.pj.squashrestapp.repository;

import java.util.List;
import java.util.UUID;

public interface SearchableByLeagueUuid {

    List<Long> fetchIdsByLeagueUuidRaw(UUID leagueUuid);
}
