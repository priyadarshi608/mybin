package org.apache.log4j.spi;













public abstract class Filter
  implements OptionHandler
{
  /**
   * @deprecated
   */
  public Filter next;
  











  public static final int DENY = -1;
  











  public static final int NEUTRAL = 0;
  











  public static final int ACCEPT = 1;
  











  public Filter() {}
  











  public void activateOptions() {}
  











  public abstract int decide(LoggingEvent paramLoggingEvent);
  











  public void setNext(Filter next)
  {
    this.next = next;
  }
  


  public Filter getNext()
  {
    return next;
  }
}
