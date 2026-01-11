package org.apache.log4j.helpers;

import java.io.FilterWriter;
import java.io.Writer;
import org.apache.log4j.spi.ErrorHandler;



























public class QuietWriter
  extends FilterWriter
{
  protected ErrorHandler errorHandler;
  
  public QuietWriter(Writer writer, ErrorHandler errorHandler)
  {
    super(writer);
    setErrorHandler(errorHandler);
  }
  
  public void write(String string)
  {
    if (string != null) {
      try {
        out.write(string);
      } catch (Exception e) {
        errorHandler.error("Failed to write [" + string + "].", e, 1);
      }
    }
  }
  
  public void flush()
  {
    try
    {
      out.flush();
    } catch (Exception e) {
      errorHandler.error("Failed to flush writer,", e, 2);
    }
  }
  


  public void setErrorHandler(ErrorHandler eh)
  {
    if (eh == null)
    {
      throw new IllegalArgumentException("Attempted to set null ErrorHandler.");
    }
    errorHandler = eh;
  }
}
