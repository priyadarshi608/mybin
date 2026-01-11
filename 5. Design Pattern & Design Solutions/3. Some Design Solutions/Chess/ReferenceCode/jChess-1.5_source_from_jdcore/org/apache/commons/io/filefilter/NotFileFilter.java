package org.apache.commons.io.filefilter;

import java.io.File;






























public class NotFileFilter
  extends AbstractFileFilter
{
  private IOFileFilter filter;
  
  public NotFileFilter(IOFileFilter filter)
  {
    if (filter == null) {
      throw new IllegalArgumentException("The filter must not be null");
    }
    this.filter = filter;
  }
  





  public boolean accept(File file)
  {
    return !filter.accept(file);
  }
  






  public boolean accept(File file, String name)
  {
    return !filter.accept(file, name);
  }
}
