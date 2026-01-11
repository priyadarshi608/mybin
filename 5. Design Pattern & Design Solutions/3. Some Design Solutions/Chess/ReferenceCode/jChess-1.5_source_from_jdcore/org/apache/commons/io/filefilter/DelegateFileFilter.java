package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;






























public class DelegateFileFilter
  extends AbstractFileFilter
{
  private FilenameFilter filenameFilter;
  private FileFilter fileFilter;
  
  public DelegateFileFilter(FilenameFilter filter)
  {
    if (filter == null) {
      throw new IllegalArgumentException("The FilenameFilter must not be null");
    }
    filenameFilter = filter;
  }
  




  public DelegateFileFilter(FileFilter filter)
  {
    if (filter == null) {
      throw new IllegalArgumentException("The FileFilter must not be null");
    }
    fileFilter = filter;
  }
  





  public boolean accept(File file)
  {
    if (fileFilter != null) {
      return fileFilter.accept(file);
    }
    return super.accept(file);
  }
  







  public boolean accept(File dir, String name)
  {
    if (filenameFilter != null) {
      return filenameFilter.accept(dir, name);
    }
    return super.accept(dir, name);
  }
}
