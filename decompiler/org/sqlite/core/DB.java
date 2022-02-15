package org.sqlite.core;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.sqlite.BusyHandler;
import org.sqlite.Function;
import org.sqlite.ProgressHandler;
import org.sqlite.SQLiteCommitListener;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import org.sqlite.SQLiteUpdateListener;





















public abstract class DB
  implements Codes
{
  private final String url;
  private final String fileName;
  private final SQLiteConfig config;
  private final AtomicBoolean closed = new AtomicBoolean(true);

  
  long begin = 0L;
  long commit = 0L;

  
  private final Map<Long, CoreStatement> stmts = new HashMap<>();
  
  private final Set<SQLiteUpdateListener> updateListeners = new HashSet<>();
  private final Set<SQLiteCommitListener> commitListeners = new HashSet<>();


  
  public DB(String url, String fileName, SQLiteConfig config) throws SQLException {
    this.url = url;
    this.fileName = fileName;
    this.config = config;
  }
  
  public String getUrl() {
    return this.url;
  }
  
  public boolean isClosed() {
    return this.closed.get();
  }
  
  public SQLiteConfig getConfig() {
    return this.config;
  }







  
  public abstract void interrupt() throws SQLException;







  
  public abstract void busy_timeout(int paramInt) throws SQLException;







  
  public abstract void busy_handler(BusyHandler paramBusyHandler) throws SQLException;







  
  abstract String errmsg() throws SQLException;







  
  public abstract String libversion() throws SQLException;







  
  public abstract int changes() throws SQLException;







  
  public abstract int total_changes() throws SQLException;







  
  public abstract int shared_cache(boolean paramBoolean) throws SQLException;







  
  public abstract int enable_load_extension(boolean paramBoolean) throws SQLException;







  
  public final synchronized void exec(String sql, boolean autoCommit) throws SQLException {
    long pointer = 0L;
    try {
      pointer = prepare(sql);
      int rc = step(pointer);
      switch (rc) {
        case 101:
          ensureAutoCommit(autoCommit);
          return;
        case 100:
          return;
      } 
      throwex(rc);
    }
    finally {
      
      finalize(pointer);
    } 
  }








  
  public final synchronized void open(String file, int openFlags) throws SQLException {
    _open(file, openFlags);
    this.closed.set(false);
    
    if (this.fileName.startsWith("file:") && !this.fileName.contains("cache="))
    {
      shared_cache(this.config.isEnabledSharedCache());
    }
    enable_load_extension(this.config.isEnabledLoadExtension());
    busy_timeout(this.config.getBusyTimeout());
  }







  
  public final synchronized void close() throws SQLException {
    synchronized (this.stmts) {
      Iterator<Map.Entry<Long, CoreStatement>> i = this.stmts.entrySet().iterator();
      while (i.hasNext()) {
        Map.Entry<Long, CoreStatement> entry = i.next();
        CoreStatement stmt = entry.getValue();
        finalize(((Long)entry.getKey()).longValue());
        if (stmt != null) {
          stmt.pointer = 0L;
        }
        i.remove();
      } 
    } 

    
    free_functions();

    
    if (this.begin != 0L) {
      finalize(this.begin);
      this.begin = 0L;
    } 
    if (this.commit != 0L) {
      finalize(this.commit);
      this.commit = 0L;
    } 
    
    this.closed.set(true);
    _close();
  }






  
  public final synchronized void prepare(CoreStatement stmt) throws SQLException {
    if (stmt.sql == null) {
      throw new NullPointerException();
    }
    if (stmt.pointer != 0L) {
      finalize(stmt);
    }
    stmt.pointer = prepare(stmt.sql);
    this.stmts.put(new Long(stmt.pointer), stmt);
  }







  
  public final synchronized int finalize(CoreStatement stmt) throws SQLException {
    if (stmt.pointer == 0L) {
      return 0;
    }
    int rc = 1;
    try {
      rc = finalize(stmt.pointer);
    } finally {
      
      this.stmts.remove(new Long(stmt.pointer));
      stmt.pointer = 0L;
    } 
    return rc;
  }








  
  protected abstract void _open(String paramString, int paramInt) throws SQLException;








  
  protected abstract void _close() throws SQLException;








  
  public abstract int _exec(String paramString) throws SQLException;








  
  protected abstract long prepare(String paramString) throws SQLException;








  
  protected abstract int finalize(long paramLong) throws SQLException;








  
  public abstract int step(long paramLong) throws SQLException;








  
  public abstract int reset(long paramLong) throws SQLException;








  
  public abstract int clear_bindings(long paramLong) throws SQLException;








  
  abstract int bind_parameter_count(long paramLong) throws SQLException;








  
  public abstract int column_count(long paramLong) throws SQLException;








  
  public abstract int column_type(long paramLong, int paramInt) throws SQLException;








  
  public abstract String column_decltype(long paramLong, int paramInt) throws SQLException;








  
  public abstract String column_table_name(long paramLong, int paramInt) throws SQLException;








  
  public abstract String column_name(long paramLong, int paramInt) throws SQLException;








  
  public abstract String column_text(long paramLong, int paramInt) throws SQLException;








  
  public abstract byte[] column_blob(long paramLong, int paramInt) throws SQLException;








  
  public abstract double column_double(long paramLong, int paramInt) throws SQLException;







  
  public abstract long column_long(long paramLong, int paramInt) throws SQLException;







  
  public abstract int column_int(long paramLong, int paramInt) throws SQLException;







  
  abstract int bind_null(long paramLong, int paramInt) throws SQLException;







  
  abstract int bind_int(long paramLong, int paramInt1, int paramInt2) throws SQLException;







  
  abstract int bind_long(long paramLong1, int paramInt, long paramLong2) throws SQLException;







  
  abstract int bind_double(long paramLong, int paramInt, double paramDouble) throws SQLException;







  
  abstract int bind_text(long paramLong, int paramInt, String paramString) throws SQLException;







  
  abstract int bind_blob(long paramLong, int paramInt, byte[] paramArrayOfbyte) throws SQLException;







  
  public abstract void result_null(long paramLong) throws SQLException;







  
  public abstract void result_text(long paramLong, String paramString) throws SQLException;







  
  public abstract void result_blob(long paramLong, byte[] paramArrayOfbyte) throws SQLException;







  
  public abstract void result_double(long paramLong, double paramDouble) throws SQLException;







  
  public abstract void result_long(long paramLong1, long paramLong2) throws SQLException;







  
  public abstract void result_int(long paramLong, int paramInt) throws SQLException;







  
  public abstract void result_error(long paramLong, String paramString) throws SQLException;







  
  public abstract String value_text(Function paramFunction, int paramInt) throws SQLException;







  
  public abstract byte[] value_blob(Function paramFunction, int paramInt) throws SQLException;







  
  public abstract double value_double(Function paramFunction, int paramInt) throws SQLException;







  
  public abstract long value_long(Function paramFunction, int paramInt) throws SQLException;







  
  public abstract int value_int(Function paramFunction, int paramInt) throws SQLException;







  
  public abstract int value_type(Function paramFunction, int paramInt) throws SQLException;







  
  public abstract int create_function(String paramString, Function paramFunction, int paramInt1, int paramInt2) throws SQLException;







  
  public abstract int destroy_function(String paramString, int paramInt) throws SQLException;







  
  abstract void free_functions() throws SQLException;







  
  public abstract int backup(String paramString1, String paramString2, ProgressObserver paramProgressObserver) throws SQLException;







  
  public abstract int restore(String paramString1, String paramString2, ProgressObserver paramProgressObserver) throws SQLException;







  
  public abstract int limit(int paramInt1, int paramInt2) throws SQLException;







  
  public abstract void register_progress_handler(int paramInt, ProgressHandler paramProgressHandler) throws SQLException;







  
  public abstract void clear_progress_handler() throws SQLException;







  
  abstract boolean[][] column_metadata(long paramLong) throws SQLException;







  
  public final synchronized String[] column_names(long stmt) throws SQLException {
    String[] names = new String[column_count(stmt)];
    for (int i = 0; i < names.length; i++) {
      names[i] = column_name(stmt, i);
    }
    return names;
  }









  
  final synchronized int sqlbind(long stmt, int pos, Object v) throws SQLException {
    pos++;
    if (v == null) {
      return bind_null(stmt, pos);
    }
    if (v instanceof Integer) {
      return bind_int(stmt, pos, ((Integer)v).intValue());
    }
    if (v instanceof Short) {
      return bind_int(stmt, pos, ((Short)v).intValue());
    }
    if (v instanceof Long) {
      return bind_long(stmt, pos, ((Long)v).longValue());
    }
    if (v instanceof Float) {
      return bind_double(stmt, pos, ((Float)v).doubleValue());
    }
    if (v instanceof Double) {
      return bind_double(stmt, pos, ((Double)v).doubleValue());
    }
    if (v instanceof String) {
      return bind_text(stmt, pos, (String)v);
    }
    if (v instanceof byte[]) {
      return bind_blob(stmt, pos, (byte[])v);
    }
    
    throw new SQLException("unexpected param type: " + v.getClass());
  }











  
  final synchronized int[] executeBatch(long stmt, int count, Object[] vals, boolean autoCommit) throws SQLException {
    if (count < 1) {
      throw new SQLException("count (" + count + ") < 1");
    }
    
    int params = bind_parameter_count(stmt);

    
    int[] changes = new int[count];
    
    try {
      for (int i = 0; i < count; i++) {
        reset(stmt);
        for (int j = 0; j < params; j++) {
          int k = sqlbind(stmt, j, vals[i * params + j]);
          if (k != 0) {
            throwex(k);
          }
        } 
        
        int rc = step(stmt);
        if (rc != 101) {
          reset(stmt);
          if (rc == 100) {
            throw new BatchUpdateException("batch entry " + i + ": query returns results", changes);
          }
          throwex(rc);
        } 
        
        changes[i] = changes();
      } 
    } finally {
      
      ensureAutoCommit(autoCommit);
    } 
    
    reset(stmt);
    return changes;
  }







  
  public final synchronized boolean execute(CoreStatement stmt, Object[] vals) throws SQLException {
    if (vals != null) {
      int params = bind_parameter_count(stmt.pointer);
      if (params > vals.length) {
        throw new SQLException("assertion failure: param count (" + params + ") > value count (" + vals.length + ")");
      }

      
      for (int i = 0; i < params; i++) {
        int rc = sqlbind(stmt.pointer, i, vals[i]);
        if (rc != 0) {
          throwex(rc);
        }
      } 
    } 
    
    int statusCode = step(stmt.pointer);
    switch (statusCode & 0xFF) {
      case 101:
        reset(stmt.pointer);
        ensureAutoCommit(stmt.conn.getAutoCommit());
        return false;
      case 100:
        return true;
      case 5:
      case 6:
      case 19:
      case 21:
        throw newSQLException(statusCode);
    } 
    finalize(stmt);
    throw newSQLException(statusCode);
  }









  
  final synchronized boolean execute(String sql, boolean autoCommit) throws SQLException {
    int statusCode = _exec(sql);
    switch (statusCode) {
      case 0:
        return false;
      case 101:
        ensureAutoCommit(autoCommit);
        return false;
      case 100:
        return true;
    } 
    throw newSQLException(statusCode);
  }










  
  public final synchronized int executeUpdate(CoreStatement stmt, Object[] vals) throws SQLException {
    try {
      if (execute(stmt, vals)) {
        throw new SQLException("query returns results");
      }
    } finally {
      if (stmt.pointer != 0L) reset(stmt.pointer); 
    } 
    return changes();
  }
  abstract void set_commit_listener(boolean paramBoolean);
  
  abstract void set_update_listener(boolean paramBoolean);
  
  public synchronized void addUpdateListener(SQLiteUpdateListener listener) {
    if (this.updateListeners.add(listener) && this.updateListeners.size() == 1) {
      set_update_listener(true);
    }
  }
  
  public synchronized void addCommitListener(SQLiteCommitListener listener) {
    if (this.commitListeners.add(listener) && this.commitListeners.size() == 1) {
      set_commit_listener(true);
    }
  }
  
  public synchronized void removeUpdateListener(SQLiteUpdateListener listener) {
    if (this.updateListeners.remove(listener) && this.updateListeners.isEmpty()) {
      set_update_listener(false);
    }
  }
  
  public synchronized void removeCommitListener(SQLiteCommitListener listener) {
    if (this.commitListeners.remove(listener) && this.commitListeners.isEmpty()) {
      set_commit_listener(false);
    }
  }

  
  void onUpdate(int type, String database, String table, long rowId) {
    Set<SQLiteUpdateListener> listeners;
    synchronized (this) {
      listeners = new HashSet<>(this.updateListeners);
    } 
    
    for (SQLiteUpdateListener listener : listeners) {
      SQLiteUpdateListener.Type operationType;
      
      switch (type) { case 18:
          operationType = SQLiteUpdateListener.Type.INSERT; break;
        case 9: operationType = SQLiteUpdateListener.Type.DELETE; break;
        case 23: operationType = SQLiteUpdateListener.Type.UPDATE; break;
        default: throw new AssertionError("Unknown type: " + type); }


      
      listener.onUpdate(operationType, database, table, rowId);
    } 
  }

  
  void onCommit(boolean commit) {
    Set<SQLiteCommitListener> listeners;
    synchronized (this) {
      listeners = new HashSet<>(this.commitListeners);
    } 
    
    for (SQLiteCommitListener listener : listeners) {
      if (commit) { listener.onCommit(); continue; }
       listener.onRollback();
    } 
  }




  
  final void throwex() throws SQLException {
    throw new SQLException(errmsg());
  }





  
  public final void throwex(int errorCode) throws SQLException {
    throw newSQLException(errorCode);
  }






  
  static final void throwex(int errorCode, String errorMessage) throws SQLiteException {
    throw newSQLException(errorCode, errorMessage);
  }







  
  public static SQLiteException newSQLException(int errorCode, String errorMessage) {
    SQLiteErrorCode code = SQLiteErrorCode.getErrorCode(errorCode);
    
    SQLiteException e = new SQLiteException(String.format("%s (%s)", new Object[] { code, errorMessage }), code);
    
    return e;
  }






  
  private SQLiteException newSQLException(int errorCode) throws SQLException {
    return newSQLException(errorCode, errmsg());
  }
































  
  final void ensureAutoCommit(boolean autoCommit) throws SQLException {
    if (!autoCommit) {
      return;
    }
    
    if (this.begin == 0L) {
      this.begin = prepare("begin;");
    }
    if (this.commit == 0L) {
      this.commit = prepare("commit;");
    }
    
    try {
      if (step(this.begin) != 101) {
        return;
      }
      
      int rc = step(this.commit);
      if (rc != 101) {
        reset(this.commit);
        throwex(rc);
      }
    
    } finally {
      
      reset(this.begin);
      reset(this.commit);
    } 
  }
  
  public static interface ProgressObserver {
    void progress(int param1Int1, int param1Int2);
  }
}
