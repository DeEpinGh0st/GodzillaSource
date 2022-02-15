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
        if (aObj1 == null && aObj2 == null)
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

  
  private class Processor
    implements Runnable
  {
    private final MyTableModel this$0;
    
    private Processor() {}
    
    public void run() {
      while (true) {
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException e) {}


        
        synchronized (MyTableModel.this.mLock) {
          if (MyTableModel.this.mPaused) {
            continue;
          }
          
          boolean toHead = true;
          boolean needUpdate = false;
          Iterator it = MyTableModel.this.mPendingEvents.iterator();
          while (it.hasNext()) {
            EventDetails event = it.next();
            MyTableModel.this.mAllEvents.add(event);
            toHead = (toHead && event == MyTableModel.this.mAllEvents.first());
            needUpdate = (needUpdate || MyTableModel.this.matchFilter(event));
          } 
          MyTableModel.this.mPendingEvents.clear();
          
          if (needUpdate) {
            MyTableModel.this.updateFilteredEvents(toHead);
          }
        } 
      } 
    }
  }



  
  private static final String[] COL_NAMES = new String[] { "Time", "Priority", "Trace", "Category", "NDC", "Message" };


  
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





  
  MyTableModel() {
    Thread t = new Thread(new Processor());
    t.setDaemon(true);
    t.start();
  }






  
  public int getRowCount() {
    synchronized (this.mLock) {
      return this.mFilteredEvents.length;
    } 
  }


  
  public int getColumnCount() {
    return COL_NAMES.length;
  }


  
  public String getColumnName(int aCol) {
    return COL_NAMES[aCol];
  }


  
  public Class getColumnClass(int aCol) {
    return (aCol == 2) ? Boolean.class : Object.class;
  }

  
  public Object getValueAt(int aRow, int aCol) {
    synchronized (this.mLock) {
      EventDetails event = this.mFilteredEvents[aRow];
      
      if (aCol == 0)
        return DATE_FORMATTER.format(new Date(event.getTimeStamp())); 
      if (aCol == 1)
        return event.getPriority(); 
      if (aCol == 2) {
        return (event.getThrowableStrRep() == null) ? Boolean.FALSE : Boolean.TRUE;
      }
      if (aCol == 3)
        return event.getCategoryName(); 
      if (aCol == 4) {
        return event.getNDC();
      }
      return event.getMessage();
    } 
  }










  
  public void setPriorityFilter(Priority aPriority) {
    synchronized (this.mLock) {
      this.mPriorityFilter = aPriority;
      updateFilteredEvents(false);
    } 
  }





  
  public void setThreadFilter(String aStr) {
    synchronized (this.mLock) {
      this.mThreadFilter = aStr.trim();
      updateFilteredEvents(false);
    } 
  }





  
  public void setMessageFilter(String aStr) {
    synchronized (this.mLock) {
      this.mMessageFilter = aStr.trim();
      updateFilteredEvents(false);
    } 
  }





  
  public void setNDCFilter(String aStr) {
    synchronized (this.mLock) {
      this.mNDCFilter = aStr.trim();
      updateFilteredEvents(false);
    } 
  }





  
  public void setCategoryFilter(String aStr) {
    synchronized (this.mLock) {
      this.mCategoryFilter = aStr.trim();
      updateFilteredEvents(false);
    } 
  }





  
  public void addEvent(EventDetails aEvent) {
    synchronized (this.mLock) {
      this.mPendingEvents.add(aEvent);
    } 
  }



  
  public void clear() {
    synchronized (this.mLock) {
      this.mAllEvents.clear();
      this.mFilteredEvents = new EventDetails[0];
      this.mPendingEvents.clear();
      fireTableDataChanged();
    } 
  }

  
  public void toggle() {
    synchronized (this.mLock) {
      this.mPaused = !this.mPaused;
    } 
  }

  
  public boolean isPaused() {
    synchronized (this.mLock) {
      return this.mPaused;
    } 
  }






  
  public EventDetails getEventDetails(int aRow) {
    synchronized (this.mLock) {
      return this.mFilteredEvents[aRow];
    } 
  }










  
  private void updateFilteredEvents(boolean aInsertedToFront) {
    long start = System.currentTimeMillis();
    List filtered = new ArrayList();
    int size = this.mAllEvents.size();
    Iterator it = this.mAllEvents.iterator();
    
    while (it.hasNext()) {
      EventDetails event = it.next();
      if (matchFilter(event)) {
        filtered.add(event);
      }
    } 
    
    EventDetails lastFirst = (this.mFilteredEvents.length == 0) ? null : this.mFilteredEvents[0];

    
    this.mFilteredEvents = filtered.<EventDetails>toArray(EMPTY_LIST);
    
    if (aInsertedToFront && lastFirst != null) {
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







  
  private boolean matchFilter(EventDetails aEvent) {
    if (aEvent.getPriority().isGreaterOrEqual(this.mPriorityFilter) && aEvent.getThreadName().indexOf(this.mThreadFilter) >= 0 && aEvent.getCategoryName().indexOf(this.mCategoryFilter) >= 0 && (this.mNDCFilter.length() == 0 || (aEvent.getNDC() != null && aEvent.getNDC().indexOf(this.mNDCFilter) >= 0))) {





      
      String rm = aEvent.getMessage();
      if (rm == null)
      {
        return (this.mMessageFilter.length() == 0);
      }
      return (rm.indexOf(this.mMessageFilter) >= 0);
    } 

    
    return false;
  }
}
