package org.apache.log4j.spi;

public abstract interface RepositorySelector
{
  public abstract LoggerRepository getLoggerRepository();
}
