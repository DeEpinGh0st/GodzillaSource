package org.sqlite.jdbc3;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.sqlite.SQLiteConnection;
import org.sqlite.SQLiteOpenMode;



public abstract class JDBC3Connection
  extends SQLiteConnection
{
  private final AtomicInteger savePoint = new AtomicInteger(0);
  
  private Map<String, Class<?>> typeMap;

  
  protected JDBC3Connection(String url, String fileName, Properties prop) throws SQLException {
    super(url, fileName, prop);
  }





  
  public String getCatalog() throws SQLException {
    checkOpen();
    return null;
  }





  
  public void setCatalog(String catalog) throws SQLException {
    checkOpen();
  }





  
  public int getHoldability() throws SQLException {
    checkOpen();
    return 2;
  }





  
  public void setHoldability(int h) throws SQLException {
    checkOpen();
    if (h != 2) {
      throw new SQLException("SQLite only supports CLOSE_CURSORS_AT_COMMIT");
    }
  }





  
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    synchronized (this) {
      if (this.typeMap == null) {
        this.typeMap = new HashMap<>();
      }
      
      return this.typeMap;
    } 
  }





  
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    synchronized (this) {
      this.typeMap = map;
    } 
  }





  
  public boolean isReadOnly() throws SQLException {
    return ((getDatabase().getConfig().getOpenModeFlags() & SQLiteOpenMode.READONLY.flag) != 0);
  }






  
  public void setReadOnly(boolean ro) throws SQLException {
    if (ro != isReadOnly()) {
      throw new SQLException("Cannot change read-only flag after establishing a connection. Use SQLiteConfig#setReadOnly and SQLiteConfig.createConnection().");
    }
  }






  
  public String nativeSQL(String sql) {
    return sql;
  }






  
  public void clearWarnings() throws SQLException {}





  
  public SQLWarning getWarnings() throws SQLException {
    return null;
  }





  
  public Statement createStatement() throws SQLException {
    return createStatement(1003, 1007, 2);
  }






  
  public Statement createStatement(int rsType, int rsConcurr) throws SQLException {
    return createStatement(rsType, rsConcurr, 2);
  }





  
  public abstract Statement createStatement(int paramInt1, int paramInt2, int paramInt3) throws SQLException;




  
  public CallableStatement prepareCall(String sql) throws SQLException {
    return prepareCall(sql, 1003, 1007, 2);
  }






  
  public CallableStatement prepareCall(String sql, int rst, int rsc) throws SQLException {
    return prepareCall(sql, rst, rsc, 2);
  }





  
  public CallableStatement prepareCall(String sql, int rst, int rsc, int rsh) throws SQLException {
    throw new SQLException("SQLite does not support Stored Procedures");
  }





  
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return prepareStatement(sql, 1003, 1007);
  }





  
  public PreparedStatement prepareStatement(String sql, int autoC) throws SQLException {
    return prepareStatement(sql);
  }





  
  public PreparedStatement prepareStatement(String sql, int[] colInds) throws SQLException {
    return prepareStatement(sql);
  }





  
  public PreparedStatement prepareStatement(String sql, String[] colNames) throws SQLException {
    return prepareStatement(sql);
  }





  
  public PreparedStatement prepareStatement(String sql, int rst, int rsc) throws SQLException {
    return prepareStatement(sql, rst, rsc, 2);
  }





  
  public abstract PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException;




  
  public Savepoint setSavepoint() throws SQLException {
    checkOpen();
    if (getAutoCommit())
    {


      
      getConnectionConfig().setAutoCommit(false);
    }
    Savepoint sp = new JDBC3Savepoint(this.savePoint.incrementAndGet());
    getDatabase().exec(String.format("SAVEPOINT %s", new Object[] { sp.getSavepointName() }), false);
    return sp;
  }





  
  public Savepoint setSavepoint(String name) throws SQLException {
    checkOpen();
    if (getAutoCommit())
    {


      
      getConnectionConfig().setAutoCommit(false);
    }
    Savepoint sp = new JDBC3Savepoint(this.savePoint.incrementAndGet(), name);
    getDatabase().exec(String.format("SAVEPOINT %s", new Object[] { sp.getSavepointName() }), false);
    return sp;
  }





  
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    checkOpen();
    if (getAutoCommit()) {
      throw new SQLException("database in auto-commit mode");
    }
    getDatabase().exec(String.format("RELEASE SAVEPOINT %s", new Object[] { savepoint.getSavepointName() }), false);
  }





  
  public void rollback(Savepoint savepoint) throws SQLException {
    checkOpen();
    if (getAutoCommit()) {
      throw new SQLException("database in auto-commit mode");
    }
    getDatabase().exec(String.format("ROLLBACK TO SAVEPOINT %s", new Object[] { savepoint.getSavepointName() }), getAutoCommit());
  }




  
  public Struct createStruct(String t, Object[] attr) throws SQLException {
    throw new SQLException("unsupported by SQLite");
  }
}
