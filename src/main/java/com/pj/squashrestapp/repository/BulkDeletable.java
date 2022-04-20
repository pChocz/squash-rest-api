package com.pj.squashrestapp.repository;

import java.util.List;

public interface BulkDeletable {

    void deleteAllByIdIn(List<Long> ids);
}
