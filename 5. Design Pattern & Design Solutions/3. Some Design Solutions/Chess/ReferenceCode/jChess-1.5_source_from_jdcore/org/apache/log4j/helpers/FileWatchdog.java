package org.apache.log4j.helpers;

import java.io.File;




































public abstract class FileWatchdog
  extends Thread
{
  public static final long DEFAULT_DELAY = 60000L;
  protected String filename;
  protected long delay = 60000L;
  
  File file;
  long lastModif = 0L;
  boolean warnedAlready = false;
  boolean interrupted = false;
  
  protected FileWatchdog(String filename)
  {
    super("FileWatchdog");
    this.filename = filename;
    file = new File(filename);
    setDaemon(true);
    checkAndConfigure();
  }
  



  public void setDelay(long delay)
  {
    this.delay = delay;
  }
  

  protected abstract void doOnChange();
  
  protected void checkAndConfigure()
  {
    boolean fileExists;
    try
    {
      fileExists = file.exists();
    } catch (SecurityException e) {
      LogLog.warn("Was not allowed to read check file existance, file:[" + filename + "].");
      
      interrupted = true;
      return;
    }
    
    if (fileExists) {
      long l = file.lastModified();
      if (l > lastModif) {
        lastModif = l;
        doOnChange();
        warnedAlready = false;
      }
    }
    else if (!warnedAlready) {
      LogLog.debug("[" + filename + "] does not exist.");
      warnedAlready = true;
    }
  }
  

  public void run()
  {
    while (!interrupted) {
      try {
        Thread.sleep(delay);
      }
      catch (InterruptedException e) {}
      
      checkAndConfigure();
    }
  }
}
