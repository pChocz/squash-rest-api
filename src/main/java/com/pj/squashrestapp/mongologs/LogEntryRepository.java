package com.pj.squashrestapp.mongologs;

import org.springframework.data.mongodb.repository.MongoRepository;

/** */
public interface LogEntryRepository extends MongoRepository<LogEntry, String> {

  // no methods yet

}
