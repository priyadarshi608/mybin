package org.apache.log4j.chainsaw;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

























class MyTableModel
  extends AbstractTableModel
{
  private static final Logger LOG = Logger.getLogger(MyTableModel.class);
  

  private static final Comparator MY_COMP = new Comparator()
  {
    public int compare(Object aObj1, Object aObj2)
    {
      if ((aObj1 == null) && (aObj2 == null))
        return 0;
      if (aObj1 == null)
        return -1;
      if (aObj2 == null) {
        return 1;
      }
      

      EventDetails le1 = (EventDetails)aObj1;
      EventDetails le2 = (EventDetails)aObj2;
      
      if (le1.getTimeStamp() < le2.getTimeStamp()) {
        return 1;
      }
      
      return -1;
    }
  };
  
  private class Processor implements Runnable
  {
    Processor(MyTableModel.1 x1)
    {
      this();
    }
    
    public void run()
    {
      for (;;) {
        try {
          Thread.sleep(1000L);
        }
        catch (InterruptedException e) {}
        

        synchronized (mLock) {
          if (!mPaused)
          {


            boolean toHead = true;
            boolean needUpdate = false;
            Iterator it = mPendingEvents.iterator();
            while (it.hasNext()) {
              EventDetails event = (EventDetails)it.next();
              mAllEvents.add(event);
              toHead = (toHead) && (event == mAllEvents.first());
              needUpdate = (needUpdate) || (MyTableModel.this.matchFilter(event));
            }
            mPendingEvents.clear();
            
            if (needUpdate) {
              MyTableModel.this.updateFilteredEvents(toHead);
            }
          }
        }
      }
    }
    
    private Processor() {}
  }
  
  private static final String[] COL_NAMES = { "Time", "Priority", "Trace", "Category", "NDC", "Message" };
  


  private static final EventDetails[] EMPTY_LIST = new EventDetails[0];
  

  private static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(3, 2);
  


  private final Object mLock = new Object();
  
  private final SortedSet mAllEvents = new TreeSet(MY_COMP);
  
  private EventDetails[] mFilteredEvents = EMPTY_LIST;
  
  private final List mPendingEvents = new ArrayList();
  
  private boolean mPaused = false;
  

  private String mThreadFilter = "";
  
  private String mMessageFilter = "";
  
  private String mNDCFilter = "";
  
  private String mCategoryFilter = "";
  
  private Priority mPriorityFilter = Priority.DEBUG;
  




  MyTableModel()
  {
    Thread t = new Thread(new Processor(null));
    t.setDaemon(true);
    t.start();
  }
  





  public int getRowCount()
  {
    synchronized (mLock) {
      return mFilteredEvents.length;
    }
  }
  

  public int getColumnCount()
  {
    return COL_NAMES.length;
  }
  

  public String getColumnName(int aCol)
  {
    return COL_NAMES[aCol];
  }
  

  public Class getColumnClass(int aCol)
  {
    return Object.class;
  }
  
  public Object getValueAt(int aRow, int aCol)
  {
    synchronized (mLock) {
      EventDetails event = mFilteredEvents[aRow];
      
      if (aCol == 0)
        return DATE_FORMATTER.format(new Date(event.getTimeStamp()));
      if (aCol == 1)
        return event.getPriority();
      if (aCol == 2) {
        return event.getThrowableStrRep() == null ? Boolean.FALSE : Boolean.TRUE;
      }
      if (aCol == 3)
        return event.getCategoryName();
      if (aCol == 4) {
        return event.getNDC();
      }
      return event.getMessage();
    }
  }
  









  public void setPriorityFilter(Priority aPriority)
  {
    synchronized (mLock) {
      mPriorityFilter = aPriority;
      updateFilteredEvents(false);
    }
  }
  




  public void setThreadFilter(String aStr)
  {
    synchronized (mLock) {
      mThreadFilter = aStr.trim();
      updateFilteredEvents(false);
    }
  }
  




  public void setMessageFilter(String aStr)
  {
    synchronized (mLock) {
      mMessageFilter = aStr.trim();
      updateFilteredEvents(false);
    }
  }
  




  public void setNDCFilter(String aStr)
  {
    synchronized (mLock) {
      mNDCFilter = aStr.trim();
      updateFilteredEvents(false);
    }
  }
  




  public void setCategoryFilter(String aStr)
  {
    synchronized (mLock) {
      mCategoryFilter = aStr.trim();
      updateFilteredEvents(false);
    }
  }
  




  public void addEvent(EventDetails aEvent)
  {
    synchronized (mLock) {
      mPendingEvents.add(aEvent);
    }
  }
  


  public void clear()
  {
    synchronized (mLock) {
      mAllEvents.clear();
      mFilteredEvents = new EventDetails[0];
      mPendingEvents.clear();
      fireTableDataChanged();
    }
  }
  
  public void toggle()
  {
    synchronized (mLock) {
      mPaused = (!mPaused);
    }
  }
  
  public boolean isPaused()
  {
    synchronized (mLock) {
      return mPaused;
    }
  }
  





  public EventDetails getEventDetails(int aRow)
  {
    synchronized (mLock) {
      return mFilteredEvents[aRow];
    }
  }
  









  private void updateFilteredEvents(boolean aInsertedToFront)
  {
    long start = System.currentTimeMillis();
    List filtered = new ArrayList();
    int size = mAllEvents.size();
    Iterator it = mAllEvents.iterator();
    
    while (it.hasNext()) {
      EventDetails event = (EventDetails)it.next();
      if (matchFilter(event)) {
        filtered.add(event);
      }
    }
    
    EventDetails lastFirst = mFilteredEvents.length == 0 ? null : mFilteredEvents[0];
    

    mFilteredEvents = ((EventDetails[])filtered.toArray(EMPTY_LIST));
    
    if ((aInsertedToFront) && (lastFirst != null)) {
      int index = filtered.indexOf(lastFirst);
      if (index < 1) {
        LOG.warn("In strange state");
        fireTableDataChanged();
      } else {
        fireTableRowsInserted(0, index - 1);
      }
    } else {
      fireTableDataChanged();
    }
    
    long end = System.currentTimeMillis();
    LOG.debug("Total time [ms]: " + (end - start) + " in update, size: " + size);
  }
  






  private boolean matchFilter(EventDetails aEvent)
  {
    if ((aEvent.getPriority().isGreaterOrEqual(mPriorityFilter)) && (aEvent.getThreadName().indexOf(mThreadFilter) >= 0) && (aEvent.getCategoryName().indexOf(mCategoryFilter) >= 0) && ((mNDCFilter.length() == 0) || ((aEvent.getNDC() != null) && (aEvent.getNDC().indexOf(mNDCFilter) >= 0))))
    {





      String rm = aEvent.getMessage();
      if (rm == null)
      {
        return mMessageFilter.length() == 0;
      }
      return rm.indexOf(mMessageFilter) >= 0;
    }
    

    return false;
  }
}
