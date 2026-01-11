package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;































public class CategoryNode
  extends DefaultMutableTreeNode
{
  private static final long serialVersionUID = 5958994817693177319L;
  protected boolean _selected = true;
  protected int _numberOfContainedRecords = 0;
  protected int _numberOfRecordsFromChildren = 0;
  protected boolean _hasFatalChildren = false;
  protected boolean _hasFatalRecords = false;
  










  public CategoryNode(String title)
  {
    setUserObject(title);
  }
  


  public String getTitle()
  {
    return (String)getUserObject();
  }
  
  public void setSelected(boolean s) {
    if (s != _selected) {
      _selected = s;
    }
  }
  
  public boolean isSelected() {
    return _selected;
  }
  
  /**
   * @deprecated
   */
  public void setAllDescendantsSelected() {
    Enumeration children = children();
    while (children.hasMoreElements()) {
      CategoryNode node = (CategoryNode)children.nextElement();
      node.setSelected(true);
      node.setAllDescendantsSelected();
    }
  }
  
  /**
   * @deprecated
   */
  public void setAllDescendantsDeSelected() {
    Enumeration children = children();
    while (children.hasMoreElements()) {
      CategoryNode node = (CategoryNode)children.nextElement();
      node.setSelected(false);
      node.setAllDescendantsDeSelected();
    }
  }
  
  public String toString() {
    return getTitle();
  }
  
  public boolean equals(Object obj) {
    if ((obj instanceof CategoryNode)) {
      CategoryNode node = (CategoryNode)obj;
      String tit1 = getTitle().toLowerCase();
      String tit2 = node.getTitle().toLowerCase();
      
      if (tit1.equals(tit2)) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode() {
    return getTitle().hashCode();
  }
  
  public void addRecord() {
    _numberOfContainedRecords += 1;
    addRecordToParent();
  }
  
  public int getNumberOfContainedRecords() {
    return _numberOfContainedRecords;
  }
  
  public void resetNumberOfContainedRecords() {
    _numberOfContainedRecords = 0;
    _numberOfRecordsFromChildren = 0;
    _hasFatalRecords = false;
    _hasFatalChildren = false;
  }
  
  public boolean hasFatalRecords() {
    return _hasFatalRecords;
  }
  
  public boolean hasFatalChildren() {
    return _hasFatalChildren;
  }
  
  public void setHasFatalRecords(boolean flag) {
    _hasFatalRecords = flag;
  }
  
  public void setHasFatalChildren(boolean flag) {
    _hasFatalChildren = flag;
  }
  



  protected int getTotalNumberOfRecords()
  {
    return getNumberOfRecordsFromChildren() + getNumberOfContainedRecords();
  }
  


  protected void addRecordFromChild()
  {
    _numberOfRecordsFromChildren += 1;
    addRecordToParent();
  }
  
  protected int getNumberOfRecordsFromChildren() {
    return _numberOfRecordsFromChildren;
  }
  
  protected void addRecordToParent() {
    TreeNode parent = getParent();
    if (parent == null) {
      return;
    }
    ((CategoryNode)parent).addRecordFromChild();
  }
}
