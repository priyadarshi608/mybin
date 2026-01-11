package org.apache.log4j;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;






































public abstract class AppenderSkeleton
  implements Appender, OptionHandler
{
  protected Layout layout;
  protected String name;
  protected Priority threshold;
  protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();
  


  protected Filter headFilter;
  


  protected Filter tailFilter;
  

  protected boolean closed = false;
  







  public AppenderSkeleton() {}
  







  protected AppenderSkeleton(boolean isActive) {}
  







  public void activateOptions() {}
  







  public void addFilter(Filter newFilter)
  {
    if (headFilter == null) {
      headFilter = (this.tailFilter = newFilter);
    } else {
      tailFilter.setNext(newFilter);
      tailFilter = newFilter;
    }
  }
  







  protected abstract void append(LoggingEvent paramLoggingEvent);
  







  public void clearFilters()
  {
    headFilter = (this.tailFilter = null);
  }
  







  public void finalize()
  {
    if (closed) {
      return;
    }
    LogLog.debug("Finalizing appender named [" + name + "].");
    close();
  }
  






  public ErrorHandler getErrorHandler()
  {
    return errorHandler;
  }
  






  public Filter getFilter()
  {
    return headFilter;
  }
  







  public final Filter getFirstFilter()
  {
    return headFilter;
  }
  



  public Layout getLayout()
  {
    return layout;
  }
  






  public final String getName()
  {
    return name;
  }
  





  public Priority getThreshold()
  {
    return threshold;
  }
  







  public boolean isAsSevereAsThreshold(Priority priority)
  {
    return (threshold == null) || (priority.isGreaterOrEqual(threshold));
  }
  







  public synchronized void doAppend(LoggingEvent event)
  {
    if (closed) {
      LogLog.error("Attempted to append to closed appender named [" + name + "].");
      return;
    }
    
    if (!isAsSevereAsThreshold(event.getLevel())) {
      return;
    }
    
    Filter f = headFilter;
    

    while (f != null) {
      switch (f.decide(event)) {
      case -1: 
        return;
      case 1:  break; case 0:  f = f.getNext();
      }
      
    }
    append(event);
  }
  





  public synchronized void setErrorHandler(ErrorHandler eh)
  {
    if (eh == null)
    {

      LogLog.warn("You have tried to set a null error-handler.");
    } else {
      errorHandler = eh;
    }
  }
  






  public void setLayout(Layout layout)
  {
    this.layout = layout;
  }
  




  public void setName(String name)
  {
    this.name = name;
  }
  










  public void setThreshold(Priority threshold)
  {
    this.threshold = threshold;
  }
}
