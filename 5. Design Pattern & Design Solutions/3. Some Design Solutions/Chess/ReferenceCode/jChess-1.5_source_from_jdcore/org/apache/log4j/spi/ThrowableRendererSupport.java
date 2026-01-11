package org.apache.log4j.spi;

public abstract interface ThrowableRendererSupport
{
  public abstract ThrowableRenderer getThrowableRenderer();
  
  public abstract void setThrowableRenderer(ThrowableRenderer paramThrowableRenderer);
}
