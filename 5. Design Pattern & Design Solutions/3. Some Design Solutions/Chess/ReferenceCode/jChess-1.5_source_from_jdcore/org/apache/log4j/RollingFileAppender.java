package org.apache.log4j;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;




































public class RollingFileAppender
  extends FileAppender
{
  protected long maxFileSize = 10485760L;
  



  protected int maxBackupIndex = 1;
  
  private long nextRollover = 0L;
  








  public RollingFileAppender() {}
  







  public RollingFileAppender(Layout layout, String filename, boolean append)
    throws IOException
  {
    super(layout, filename, append);
  }
  





  public RollingFileAppender(Layout layout, String filename)
    throws IOException
  {
    super(layout, filename);
  }
  



  public int getMaxBackupIndex()
  {
    return maxBackupIndex;
  }
  






  public long getMaximumFileSize()
  {
    return maxFileSize;
  }
  

















  public void rollOver()
  {
    if (qw != null) {
      long size = ((CountingQuietWriter)qw).getCount();
      LogLog.debug("rolling over count=" + size);
      

      nextRollover = (size + maxFileSize);
    }
    LogLog.debug("maxBackupIndex=" + maxBackupIndex);
    
    boolean renameSucceeded = true;
    
    if (maxBackupIndex > 0)
    {
      File file = new File(fileName + '.' + maxBackupIndex);
      if (file.exists()) {
        renameSucceeded = file.delete();
      }
      
      for (int i = maxBackupIndex - 1; (i >= 1) && (renameSucceeded); i--) {
        file = new File(fileName + "." + i);
        if (file.exists()) {
          File target = new File(fileName + '.' + (i + 1));
          LogLog.debug("Renaming file " + file + " to " + target);
          renameSucceeded = file.renameTo(target);
        }
      }
      
      if (renameSucceeded)
      {
        File target = new File(fileName + "." + 1);
        
        closeFile();
        
        file = new File(fileName);
        LogLog.debug("Renaming file " + file + " to " + target);
        renameSucceeded = file.renameTo(target);
        


        if (!renameSucceeded) {
          try {
            setFile(fileName, true, bufferedIO, bufferSize);
          }
          catch (IOException e) {
            if ((e instanceof InterruptedIOException)) {
              Thread.currentThread().interrupt();
            }
            LogLog.error("setFile(" + fileName + ", true) call failed.", e);
          }
        }
      }
    }
    



    if (renameSucceeded)
    {
      try
      {
        setFile(fileName, false, bufferedIO, bufferSize);
        nextRollover = 0L;
      }
      catch (IOException e) {
        if ((e instanceof InterruptedIOException)) {
          Thread.currentThread().interrupt();
        }
        LogLog.error("setFile(" + fileName + ", false) call failed.", e);
      }
    }
  }
  

  public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
    throws IOException
  {
    super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
    if (append) {
      File f = new File(fileName);
      ((CountingQuietWriter)qw).setCount(f.length());
    }
  }
  










  public void setMaxBackupIndex(int maxBackups)
  {
    maxBackupIndex = maxBackups;
  }
  












  public void setMaximumFileSize(long maxFileSize)
  {
    this.maxFileSize = maxFileSize;
  }
  












  public void setMaxFileSize(String value)
  {
    maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1L);
  }
  
  protected void setQWForFiles(Writer writer)
  {
    qw = new CountingQuietWriter(writer, errorHandler);
  }
  






  protected void subAppend(LoggingEvent event)
  {
    super.subAppend(event);
    if ((fileName != null) && (qw != null)) {
      long size = ((CountingQuietWriter)qw).getCount();
      if ((size >= maxFileSize) && (size >= nextRollover)) {
        rollOver();
      }
    }
  }
}
