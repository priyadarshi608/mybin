package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
























public class DemuxInputStream
  extends InputStream
{
  private InheritableThreadLocal m_streams = new InheritableThreadLocal();
  


  public DemuxInputStream() {}
  


  public InputStream bindStream(InputStream input)
  {
    InputStream oldValue = getStream();
    m_streams.set(input);
    return oldValue;
  }
  





  public void close()
    throws IOException
  {
    InputStream input = getStream();
    if (null != input)
    {
      input.close();
    }
  }
  






  public int read()
    throws IOException
  {
    InputStream input = getStream();
    if (null != input)
    {
      return input.read();
    }
    

    return -1;
  }
  






  private InputStream getStream()
  {
    return (InputStream)m_streams.get();
  }
}
