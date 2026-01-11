package org.apache.commons.io.filefilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


































public class AndFileFilter
  extends AbstractFileFilter
  implements ConditionalFileFilter
{
  private List fileFilters;
  
  public AndFileFilter()
  {
    fileFilters = new ArrayList();
  }
  






  public AndFileFilter(List fileFilters)
  {
    if (fileFilters == null) {
      this.fileFilters = new ArrayList();
    } else {
      this.fileFilters = new ArrayList(fileFilters);
    }
  }
  






  public AndFileFilter(IOFileFilter filter1, IOFileFilter filter2)
  {
    if ((filter1 == null) || (filter2 == null)) {
      throw new IllegalArgumentException("The filters must not be null");
    }
    fileFilters = new ArrayList();
    addFileFilter(filter1);
    addFileFilter(filter2);
  }
  


  public void addFileFilter(IOFileFilter ioFileFilter)
  {
    fileFilters.add(ioFileFilter);
  }
  


  public List getFileFilters()
  {
    return Collections.unmodifiableList(fileFilters);
  }
  


  public boolean removeFileFilter(IOFileFilter ioFileFilter)
  {
    return fileFilters.remove(ioFileFilter);
  }
  


  public void setFileFilters(List fileFilters)
  {
    this.fileFilters = new ArrayList(fileFilters);
  }
  


  public boolean accept(File file)
  {
    if (fileFilters.size() == 0) {
      return false;
    }
    for (Iterator iter = fileFilters.iterator(); iter.hasNext();) {
      IOFileFilter fileFilter = (IOFileFilter)iter.next();
      if (!fileFilter.accept(file)) {
        return false;
      }
    }
    return true;
  }
  


  public boolean accept(File file, String name)
  {
    if (fileFilters.size() == 0) {
      return false;
    }
    for (Iterator iter = fileFilters.iterator(); iter.hasNext();) {
      IOFileFilter fileFilter = (IOFileFilter)iter.next();
      if (!fileFilter.accept(file, name)) {
        return false;
      }
    }
    return true;
  }
}
