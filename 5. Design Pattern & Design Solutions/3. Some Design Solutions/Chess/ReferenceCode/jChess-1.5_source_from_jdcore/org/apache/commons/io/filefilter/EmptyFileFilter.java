package org.apache.commons.io.filefilter;

import java.io.File;

















































public class EmptyFileFilter
  extends AbstractFileFilter
{
  public static final IOFileFilter EMPTY = new EmptyFileFilter();
  

  public static final IOFileFilter NOT_EMPTY = new NotFileFilter(EMPTY);
  





  protected EmptyFileFilter() {}
  





  public boolean accept(File file)
  {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      return (files == null) || (files.length == 0);
    }
    return file.length() == 0L;
  }
}
