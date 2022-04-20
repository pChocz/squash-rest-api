package com.pj.squashrestapp.repository;

import java.util.List;
import java.util.UUID;

public interface SearchableBySeasonUuid {

    List<Long> fetchIdsBySeasonUuidRaw(UUID seasonUuid);
}
