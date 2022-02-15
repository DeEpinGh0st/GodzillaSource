package org.sqlite.core;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.sqlite.SQLiteConnectionConfig;




















public abstract class CoreResultSet
  implements Codes
{
  protected final CoreStatement stmt;
  public boolean open = false;
  public int maxRows;
  public String[] cols = null;
  public String[] colsMeta = null;
  protected boolean[][] meta = (boolean[][])null;
  
  protected int limitRows;
  protected int row = 0;
  
  protected int lastCol;
  public boolean closeStmt;
  protected Map<String, Integer> columnNameToIndex = null;




  
  protected CoreResultSet(CoreStatement stmt) {
    this.stmt = stmt;
  }


  
  protected DB getDatabase() {
    return this.stmt.getDatbase();
  }
  
  protected SQLiteConnectionConfig getConnectionConfig() {
    return this.stmt.getConnectionConfig();
  }




  
  public boolean isOpen() {
    return this.open;
  }



  
  protected void checkOpen() throws SQLException {
    if (!this.open) {
      throw new SQLException("ResultSet closed");
    }
  }






  
  public int checkCol(int col) throws SQLException {
    if (this.colsMeta == null) {
      throw new IllegalStateException("SQLite JDBC: inconsistent internal state");
    }
    if (col < 1 || col > this.colsMeta.length) {
      throw new SQLException("column " + col + " out of bounds [1," + this.colsMeta.length + "]");
    }
    return --col;
  }






  
  protected int markCol(int col) throws SQLException {
    checkOpen();
    checkCol(col);
    this.lastCol = col;
    return --col;
  }



  
  public void checkMeta() throws SQLException {
    checkCol(1);
    if (this.meta == null) {
      this.meta = this.stmt.getDatbase().column_metadata(this.stmt.pointer);
    }
  }
  
  public void close() throws SQLException {
    this.cols = null;
    this.colsMeta = null;
    this.meta = (boolean[][])null;
    this.limitRows = 0;
    this.row = 0;
    this.lastCol = -1;
    this.columnNameToIndex = null;
    
    if (!this.open) {
      return;
    }
    
    DB db = this.stmt.getDatbase();
    synchronized (db) {
      if (this.stmt.pointer != 0L) {
        db.reset(this.stmt.pointer);
        
        if (this.closeStmt) {
          this.closeStmt = false;
          ((Statement)this.stmt).close();
        } 
      } 
    } 
    
    this.open = false;
  }
  
  protected Integer findColumnIndexInCache(String col) {
    if (this.columnNameToIndex == null) {
      return null;
    }
    return this.columnNameToIndex.get(col);
  }
  
  protected int addColumnIndexInCache(String col, int index) {
    if (this.columnNameToIndex == null) {
      this.columnNameToIndex = new HashMap<>(this.cols.length);
    }
    this.columnNameToIndex.put(col, Integer.valueOf(index));
    return index;
  }
}
