package org.apache.log4j.lf5;

public abstract interface LogRecordFilter
{
  public abstract boolean passes(LogRecord paramLogRecord);
}
