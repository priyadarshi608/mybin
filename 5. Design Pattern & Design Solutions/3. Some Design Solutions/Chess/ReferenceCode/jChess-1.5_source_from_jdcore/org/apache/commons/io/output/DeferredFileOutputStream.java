package org.apache.commons.io.output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;



























































public class DeferredFileOutputStream
  extends ThresholdingOutputStream
{
  private ByteArrayOutputStream memoryOutputStream;
  private OutputStream currentOutputStream;
  private File outputFile;
  private boolean closed = false;
  










  public DeferredFileOutputStream(int threshold, File outputFile)
  {
    super(threshold);
    this.outputFile = outputFile;
    
    memoryOutputStream = new ByteArrayOutputStream();
    currentOutputStream = memoryOutputStream;
  }
  











  protected OutputStream getStream()
    throws IOException
  {
    return currentOutputStream;
  }
  








  protected void thresholdReached()
    throws IOException
  {
    FileOutputStream fos = new FileOutputStream(outputFile);
    memoryOutputStream.writeTo(fos);
    currentOutputStream = fos;
    memoryOutputStream = null;
  }
  











  public boolean isInMemory()
  {
    return !isThresholdExceeded();
  }
  









  public byte[] getData()
  {
    if (memoryOutputStream != null)
    {
      return memoryOutputStream.toByteArray();
    }
    return null;
  }
  








  public File getFile()
  {
    return outputFile;
  }
  





  public void close()
    throws IOException
  {
    super.close();
    closed = true;
  }
  










  public void writeTo(OutputStream out)
    throws IOException
  {
    if (!closed)
    {
      throw new IOException("Stream not closed");
    }
    
    if (isInMemory())
    {
      memoryOutputStream.writeTo(out);
    }
    else
    {
      FileInputStream fis = new FileInputStream(outputFile);
      try {
        IOUtils.copy(fis, out);
      } finally {
        IOUtils.closeQuietly(fis);
      }
    }
  }
}
