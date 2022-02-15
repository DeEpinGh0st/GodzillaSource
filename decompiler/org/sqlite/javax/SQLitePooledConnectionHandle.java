package org.sqlite.javax;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.DB;
import org.sqlite.jdbc4.JDBC4PreparedStatement;
import org.sqlite.jdbc4.JDBC4Statement;












































































































































class SQLitePooledConnectionHandle
  extends SQLiteConnection
{
  private final SQLitePooledConnection parent;
  private final AtomicBoolean isClosed = new AtomicBoolean(false);
  
  public SQLitePooledConnectionHandle(SQLitePooledConnection parent) {
    super(parent.getPhysicalConn().getDatabase());
    this.parent = parent;
  }



  
  public Statement createStatement() throws SQLException {
    return (Statement)new JDBC4Statement(this);
  }



  
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return (PreparedStatement)new JDBC4PreparedStatement(this, sql);
  }



  
  public CallableStatement prepareCall(String sql) throws SQLException {
    return null;
  }



  
  public String nativeSQL(String sql) throws SQLException {
    return null;
  }




  
  public void setAutoCommit(boolean autoCommit) throws SQLException {}




  
  public boolean getAutoCommit() throws SQLException {
    return false;
  }





  
  public void commit() throws SQLException {}




  
  public void rollback() throws SQLException {}




  
  public void close() throws SQLException {
    ConnectionEvent event = new ConnectionEvent((PooledConnection)this.parent);
    
    List<ConnectionEventListener> listeners = this.parent.getListeners();
    for (int i = listeners.size() - 1; i >= 0; i--) {
      ((ConnectionEventListener)listeners.get(i)).connectionClosed(event);
    }
    
    if (!this.parent.getPhysicalConn().getAutoCommit()) {
      this.parent.getPhysicalConn().rollback();
    }
    this.parent.getPhysicalConn().setAutoCommit(true);
    this.isClosed.set(true);
  }

  
  public boolean isClosed() {
    return this.isClosed.get();
  }



  
  public DatabaseMetaData getMetaData() throws SQLException {
    return null;
  }




  
  public void setReadOnly(boolean readOnly) throws SQLException {}




  
  public boolean isReadOnly() throws SQLException {
    return false;
  }




  
  public void setCatalog(String catalog) throws SQLException {}




  
  public String getCatalog() throws SQLException {
    return null;
  }




  
  public void setTransactionIsolation(int level) throws SQLException {}



  
  public int getTransactionIsolation() {
    return 0;
  }



  
  public SQLWarning getWarnings() throws SQLException {
    return null;
  }




  
  public void clearWarnings() throws SQLException {}




  
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    return null;
  }



  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    return null;
  }



  
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    return null;
  }



  
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return null;
  }





  
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {}




  
  public void setHoldability(int holdability) throws SQLException {}




  
  public int getHoldability() throws SQLException {
    return 0;
  }



  
  public Savepoint setSavepoint() throws SQLException {
    return null;
  }



  
  public Savepoint setSavepoint(String name) throws SQLException {
    return null;
  }





  
  public void rollback(Savepoint savepoint) throws SQLException {}




  
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {}




  
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return null;
  }



  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return null;
  }



  
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return null;
  }



  
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    return null;
  }



  
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    return null;
  }



  
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    return null;
  }



  
  public Clob createClob() throws SQLException {
    return null;
  }



  
  public Blob createBlob() throws SQLException {
    return null;
  }



  
  public NClob createNClob() throws SQLException {
    return null;
  }



  
  public SQLXML createSQLXML() throws SQLException {
    return null;
  }



  
  public boolean isValid(int timeout) throws SQLException {
    return false;
  }





  
  public void setClientInfo(String name, String value) throws SQLClientInfoException {}




  
  public void setClientInfo(Properties properties) throws SQLClientInfoException {}




  
  public String getClientInfo(String name) throws SQLException {
    return null;
  }



  
  public Properties getClientInfo() throws SQLException {
    return null;
  }



  
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    return null;
  }



  
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    return null;
  }




  
  public void setSchema(String schema) throws SQLException {}




  
  public String getSchema() throws SQLException {
    return null;
  }





  
  public void abort(Executor executor) throws SQLException {}




  
  public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {}




  
  public int getNetworkTimeout() throws SQLException {
    return 0;
  }



  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }



  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }


  
  public int getBusyTimeout() {
    return 0;
  }



  
  public void setBusyTimeout(int timeoutMillis) {}



  
  public DB getDatabase() {
    return null;
  }
}
