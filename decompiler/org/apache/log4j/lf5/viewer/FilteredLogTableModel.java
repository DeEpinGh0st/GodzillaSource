package org.apache.log4j.lf5.viewer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.lf5.LogRecord;
import org.apache.log4j.lf5.LogRecordFilter;
import org.apache.log4j.lf5.PassingLogRecordFilter;



































public class FilteredLogTableModel
  extends AbstractTableModel
{
  protected LogRecordFilter _filter = (LogRecordFilter)new PassingLogRecordFilter();
  protected List _allRecords = new ArrayList();
  protected List _filteredRecords;
  protected int _maxNumberOfLogRecords = 5000;
  protected String[] _colNames = new String[] { "Date", "Thread", "Message #", "Level", "NDC", "Category", "Message", "Location", "Thrown" };
























  
  public void setLogRecordFilter(LogRecordFilter filter) {
    this._filter = filter;
  }
  
  public LogRecordFilter getLogRecordFilter() {
    return this._filter;
  }
  
  public String getColumnName(int i) {
    return this._colNames[i];
  }
  
  public int getColumnCount() {
    return this._colNames.length;
  }
  
  public int getRowCount() {
    return getFilteredRecords().size();
  }
  
  public int getTotalRowCount() {
    return this._allRecords.size();
  }
  
  public Object getValueAt(int row, int col) {
    LogRecord record = getFilteredRecord(row);
    return getColumn(col, record);
  }
  
  public void setMaxNumberOfLogRecords(int maxNumRecords) {
    if (maxNumRecords > 0) {
      this._maxNumberOfLogRecords = maxNumRecords;
    }
  }


  
  public synchronized boolean addLogRecord(LogRecord record) {
    this._allRecords.add(record);
    
    if (!this._filter.passes(record)) {
      return false;
    }
    getFilteredRecords().add(record);
    fireTableRowsInserted(getRowCount(), getRowCount());
    trimRecords();
    return true;
  }




  
  public synchronized void refresh() {
    this._filteredRecords = createFilteredRecordsList();
    fireTableDataChanged();
  }
  
  public synchronized void fastRefresh() {
    this._filteredRecords.remove(0);
    fireTableRowsDeleted(0, 0);
  }




  
  public synchronized void clear() {
    this._allRecords.clear();
    this._filteredRecords.clear();
    fireTableDataChanged();
  }




  
  protected List getFilteredRecords() {
    if (this._filteredRecords == null) {
      refresh();
    }
    return this._filteredRecords;
  }
  
  protected List createFilteredRecordsList() {
    List result = new ArrayList();
    Iterator records = this._allRecords.iterator();
    
    while (records.hasNext()) {
      LogRecord current = records.next();
      if (this._filter.passes(current)) {
        result.add(current);
      }
    } 
    return result;
  }
  
  protected LogRecord getFilteredRecord(int row) {
    List records = getFilteredRecords();
    int size = records.size();
    if (row < size) {
      return records.get(row);
    }



    
    return records.get(size - 1);
  }

  
  protected Object getColumn(int col, LogRecord lr) {
    if (lr == null) {
      return "NULL Column";
    }
    String date = (new Date(lr.getMillis())).toString();
    switch (col) {
      case 0:
        return date + " (" + lr.getMillis() + ")";
      case 1:
        return lr.getThreadDescription();
      case 2:
        return new Long(lr.getSequenceNumber());
      case 3:
        return lr.getLevel();
      case 4:
        return lr.getNDC();
      case 5:
        return lr.getCategory();
      case 6:
        return lr.getMessage();
      case 7:
        return lr.getLocation();
      case 8:
        return lr.getThrownStackTrace();
    } 
    String message = "The column number " + col + "must be between 0 and 8";
    throw new IllegalArgumentException(message);
  }







  
  protected void trimRecords() {
    if (needsTrimming()) {
      trimOldestRecords();
    }
  }
  
  protected boolean needsTrimming() {
    return (this._allRecords.size() > this._maxNumberOfLogRecords);
  }
  
  protected void trimOldestRecords() {
    synchronized (this._allRecords) {
      int trim = numberOfRecordsToTrim();
      if (trim > 1) {
        List oldRecords = this._allRecords.subList(0, trim);
        
        oldRecords.clear();
        refresh();
      } else {
        this._allRecords.remove(0);
        fastRefresh();
      } 
    } 
  }




  
  private int numberOfRecordsToTrim() {
    return this._allRecords.size() - this._maxNumberOfLogRecords;
  }
}
