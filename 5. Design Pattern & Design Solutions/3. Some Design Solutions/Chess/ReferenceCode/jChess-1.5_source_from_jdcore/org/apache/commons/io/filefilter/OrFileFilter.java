package org.apache.commons.io.filefilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


































public class OrFileFilter
  extends AbstractFileFilter
  implements ConditionalFileFilter
{
  private List fileFilters;
  
  public OrFileFilter()
  {
    fileFilters = new ArrayList();
  }
  






  public OrFileFilter(List fileFilters)
  {
    if (fileFilters == null) {
      this.fileFilters = new ArrayList();
    } else {
      this.fileFilters = new ArrayList(fileFilters);
    }
  }
  






  public OrFileFilter(IOFileFilter filter1, IOFileFilter filter2)
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
    this.fileFilters = fileFilters;
  }
  


  public boolean accept(File file)
  {
    for (Iterator iter = fileFilters.iterator(); iter.hasNext();) {
      IOFileFilter fileFilter = (IOFileFilter)iter.next();
      if (fileFilter.accept(file)) {
        return true;
      }
    }
    return false;
  }
  


  public boolean accept(File file, String name)
  {
    for (Iterator iter = fileFilters.iterator(); iter.hasNext();) {
      IOFileFilter fileFilter = (IOFileFilter)iter.next();
      if (fileFilter.accept(file, name)) {
        return true;
      }
    }
    return false;
  }
}
