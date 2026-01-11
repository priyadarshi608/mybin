package org.apache.log4j.spi;

public abstract interface TriggeringEventEvaluator
{
  public abstract boolean isTriggeringEvent(LoggingEvent paramLoggingEvent);
}
