package com.pj.squashrestapp.mongologs;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogConstants {

  // database
  public static final String DB_NAME = "logs";
  public static final String COLLECTION_NAME = "logEntries";

  // field names
  public static final String FIELD_ID = "id";
  public static final String FIELD_TIMESTAMP = "timestamp";
  public static final String FIELD_USERNAME = "username";
  public static final String FIELD_CLASS_NAME = "className";
  public static final String FIELD_METHOD_NAME = "methodName";
  public static final String FIELD_ARGUMENTS = "arguments";
  public static final String FIELD_DURATION = "duration";
  public static final String FIELD_QUERY_COUNT = "queryCount";
  public static final String FIELD_IS_EXCEPTION = "isException";
  public static final String FIELD_MESSAGE = "message";
  public static final String FIELD_TYPE = "type";

  // aggregate
  public static final String FIELD_AGGREGATE_SUM_DURATION = "durationSum";
  public static final String FIELD_AGGREGATE_AVG_DURATION = "durationAvg";
  public static final String FIELD_AGGREGATE_SUM_QUERY_COUNT = "queryCountSum";
  public static final String FIELD_AGGREGATE_SUM_COUNT = "countSum";
}
