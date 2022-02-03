package com.pj.squashrestapp.repositorymongo;

import com.pj.squashrestapp.model.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

/** */
public interface LogEntryRepository extends MongoRepository<LogEntry, String> {

  // no methods yet

}
