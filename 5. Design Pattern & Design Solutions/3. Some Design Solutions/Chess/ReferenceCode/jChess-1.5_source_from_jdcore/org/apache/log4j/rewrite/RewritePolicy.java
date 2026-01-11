package org.apache.log4j.rewrite;

import org.apache.log4j.spi.LoggingEvent;

public abstract interface RewritePolicy
{
  public abstract LoggingEvent rewrite(LoggingEvent paramLoggingEvent);
}
