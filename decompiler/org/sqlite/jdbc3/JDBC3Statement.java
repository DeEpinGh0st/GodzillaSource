package org.sqlite.jdbc3;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import org.sqlite.ExtendedCommand;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.CoreStatement;
import org.sqlite.core.DB;




public abstract class JDBC3Statement
  extends CoreStatement
{
  protected JDBC3Statement(SQLiteConnection conn) {
    super(conn);
  }



  
  public void close() throws SQLException {
    internalClose();
  }



  
  public boolean execute(String sql) throws SQLException {
    internalClose();
    
    ExtendedCommand.SQLExtension ext = ExtendedCommand.parse(sql);
    if (ext != null) {
      ext.execute(this.conn.getDatabase());
      
      return false;
    } 
    
    this.sql = sql;
    
    this.conn.getDatabase().prepare(this);
    return exec();
  }




  
  public ResultSet executeQuery(String sql, boolean closeStmt) throws SQLException {
    this.rs.closeStmt = closeStmt;
    
    return executeQuery(sql);
  }



  
  public ResultSet executeQuery(String sql) throws SQLException {
    internalClose();
    this.sql = sql;
    
    this.conn.getDatabase().prepare(this);
    
    if (!exec()) {
      internalClose();
      throw new SQLException("query does not return ResultSet", "SQLITE_DONE", 101);
    } 
    
    return getResultSet();
  }
  
  static class BackupObserver
    implements DB.ProgressObserver {
    public void progress(int remaining, int pageCount) {
      System.out.println(String.format("remaining:%d, page count:%d", new Object[] { Integer.valueOf(remaining), Integer.valueOf(pageCount) }));
    }
  }



  
  public int executeUpdate(String sql) throws SQLException {
    internalClose();
    this.sql = sql;
    DB db = this.conn.getDatabase();
    
    int changes = 0;
    ExtendedCommand.SQLExtension ext = ExtendedCommand.parse(sql);
    if (ext != null) {
      
      ext.execute(db);
    } else {
      
      try {
        changes = db.total_changes();

        
        int statusCode = db._exec(sql);
        if (statusCode != 0) {
          throw DB.newSQLException(statusCode, "");
        }
        changes = db.total_changes() - changes;
      } finally {
        
        internalClose();
      } 
    } 
    return changes;
  }



  
  public ResultSet getResultSet() throws SQLException {
    checkOpen();
    
    if (this.rs.isOpen()) {
      throw new SQLException("ResultSet already requested");
    }
    DB db = this.conn.getDatabase();
    
    if (db.column_count(this.pointer) == 0) {
      return null;
    }
    
    if (this.rs.colsMeta == null) {
      this.rs.colsMeta = db.column_names(this.pointer);
    }
    
    this.rs.cols = this.rs.colsMeta;
    this.rs.open = this.resultsWaiting;
    this.resultsWaiting = false;
    
    return (ResultSet)this.rs;
  }






  
  public int getUpdateCount() throws SQLException {
    DB db = this.conn.getDatabase();
    if (this.pointer != 0L && !this.rs.isOpen() && !this.resultsWaiting && db.column_count(this.pointer) == 0)
      return db.changes(); 
    return -1;
  }



  
  public void addBatch(String sql) throws SQLException {
    internalClose();
    if (this.batch == null || this.batchPos + 1 >= this.batch.length) {
      Object[] nb = new Object[Math.max(10, this.batchPos * 2)];
      if (this.batch != null)
        System.arraycopy(this.batch, 0, nb, 0, this.batch.length); 
      this.batch = nb;
    } 
    this.batch[this.batchPos++] = sql;
  }



  
  public void clearBatch() throws SQLException {
    this.batchPos = 0;
    if (this.batch != null) {
      for (int i = 0; i < this.batch.length; i++) {
        this.batch[i] = null;
      }
    }
  }


  
  public int[] executeBatch() throws SQLException {
    internalClose();
    if (this.batch == null || this.batchPos == 0) {
      return new int[0];
    }
    int[] changes = new int[this.batchPos];
    DB db = this.conn.getDatabase();
    synchronized (db) {
      try {
        for (int i = 0; i < changes.length; i++) {
          
          try { this.sql = (String)this.batch[i];
            db.prepare(this);
            changes[i] = db.executeUpdate(this, null);




            
            db.finalize(this); } catch (SQLException e) { throw new BatchUpdateException("batch entry " + i + ": " + e.getMessage(), changes); } finally { db.finalize(this); }
        
        } 
      } finally {
        
        clearBatch();
      } 
    } 
    
    return changes;
  }



  
  public void setCursorName(String name) {}



  
  public SQLWarning getWarnings() throws SQLException {
    return null;
  }



  
  public void clearWarnings() throws SQLException {}



  
  public Connection getConnection() throws SQLException {
    return (Connection)this.conn;
  }



  
  public void cancel() throws SQLException {
    this.conn.getDatabase().interrupt();
  }



  
  public int getQueryTimeout() throws SQLException {
    return this.conn.getBusyTimeout();
  }



  
  public void setQueryTimeout(int seconds) throws SQLException {
    if (seconds < 0)
      throw new SQLException("query timeout must be >= 0"); 
    this.conn.setBusyTimeout(1000 * seconds);
  }





  
  public int getMaxRows() throws SQLException {
    return this.rs.maxRows;
  }




  
  public void setMaxRows(int max) throws SQLException {
    if (max < 0)
      throw new SQLException("max row count must be >= 0"); 
    this.rs.maxRows = max;
  }



  
  public int getMaxFieldSize() throws SQLException {
    return 0;
  }



  
  public void setMaxFieldSize(int max) throws SQLException {
    if (max < 0) {
      throw new SQLException("max field size " + max + " cannot be negative");
    }
  }


  
  public int getFetchSize() throws SQLException {
    return ((ResultSet)this.rs).getFetchSize();
  }



  
  public void setFetchSize(int r) throws SQLException {
    ((ResultSet)this.rs).setFetchSize(r);
  }



  
  public int getFetchDirection() throws SQLException {
    return ((ResultSet)this.rs).getFetchDirection();
  }



  
  public void setFetchDirection(int d) throws SQLException {
    ((ResultSet)this.rs).setFetchDirection(d);
  }






  
  public ResultSet getGeneratedKeys() throws SQLException {
    return this.conn.getSQLiteDatabaseMetaData().getGeneratedKeys();
  }




  
  public boolean getMoreResults() throws SQLException {
    return getMoreResults(0);
  }



  
  public boolean getMoreResults(int c) throws SQLException {
    checkOpen();
    internalClose();
    return false;
  }



  
  public int getResultSetConcurrency() throws SQLException {
    return 1007;
  }



  
  public int getResultSetHoldability() throws SQLException {
    return 2;
  }



  
  public int getResultSetType() throws SQLException {
    return 1003;
  }



  
  public void setEscapeProcessing(boolean enable) throws SQLException {
    if (enable) {
      throw unused();
    }
  }
  
  protected SQLException unused() {
    return new SQLException("not implemented by SQLite JDBC driver");
  }



  
  public boolean execute(String sql, int[] colinds) throws SQLException {
    throw unused();
  } public boolean execute(String sql, String[] colnames) throws SQLException {
    throw unused();
  } public int executeUpdate(String sql, int autoKeys) throws SQLException {
    throw unused();
  } public int executeUpdate(String sql, int[] colinds) throws SQLException {
    throw unused();
  } public int executeUpdate(String sql, String[] cols) throws SQLException {
    throw unused();
  } public boolean execute(String sql, int autokeys) throws SQLException {
    throw unused();
  }
}
