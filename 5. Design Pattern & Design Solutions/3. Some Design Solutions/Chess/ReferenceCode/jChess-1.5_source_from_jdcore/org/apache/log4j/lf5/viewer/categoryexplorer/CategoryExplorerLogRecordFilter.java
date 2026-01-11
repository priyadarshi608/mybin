package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.util.Enumeration;
import org.apache.log4j.lf5.LogRecord;
import org.apache.log4j.lf5.LogRecordFilter;







































public class CategoryExplorerLogRecordFilter
  implements LogRecordFilter
{
  protected CategoryExplorerModel _model;
  
  public CategoryExplorerLogRecordFilter(CategoryExplorerModel model)
  {
    _model = model;
  }
  









  public boolean passes(LogRecord record)
  {
    CategoryPath path = new CategoryPath(record.getCategory());
    return _model.isCategoryPathActive(path);
  }
  


  public void reset()
  {
    resetAllNodes();
  }
  



  protected void resetAllNodes()
  {
    Enumeration nodes = _model.getRootCategoryNode().depthFirstEnumeration();
    
    while (nodes.hasMoreElements()) {
      CategoryNode current = (CategoryNode)nodes.nextElement();
      current.resetNumberOfContainedRecords();
      _model.nodeChanged(current);
    }
  }
}
