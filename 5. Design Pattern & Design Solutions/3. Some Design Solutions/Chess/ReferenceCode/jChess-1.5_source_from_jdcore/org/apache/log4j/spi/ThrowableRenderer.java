package org.apache.log4j.spi;

public abstract interface ThrowableRenderer
{
  public abstract String[] doRender(Throwable paramThrowable);
}
