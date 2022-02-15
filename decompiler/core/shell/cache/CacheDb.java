package core.shell.cache;

import core.ApplicationContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import util.Log;
import util.functions;




public class CacheDb
{
  private static final String CREATE_CACHEREQUEST_TABLE = "create table cacheRequest(requestMd5 text not null,response blob not null,PRIMARY KEY (\"requestMd5\"));";
  private Connection dbConn;
  private static final String Drivde = "org.sqlite.JDBC";
  private String DB_URL;
  private String shellId;
  
  public CacheDb(String shellId) {
    try {
      Class.forName("org.sqlite.JDBC");
      
      this.DB_URL = String.format("jdbc:sqlite:%s/%s/cache.db", new Object[] { "GodzillaCache", shellId });
      this.dbConn = DriverManager.getConnection(this.DB_URL);
      this.dbConn.setAutoCommit(true);
      functions.addShutdownHook(CacheDb.class, this);
      
      if (!tableExists("cacheRequest")) {
        getPreparedStatement("create table cacheRequest(requestMd5 text not null,response blob not null,PRIMARY KEY (\"requestMd5\"));").execute();
      }
    }
    catch (Exception e) {
      Log.error(e);
    } 
  }
  
  public boolean tableExists(String tableName) {
    String selectTable = "SELECT COUNT(1) as result FROM sqlite_master WHERE name=?";
    boolean ret = false;
    try {
      PreparedStatement preparedStatement = getPreparedStatement(selectTable);
      preparedStatement.setString(1, tableName);
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      int result = resultSet.getInt("result");
      if (result == 1) {
        ret = true;
      }
      resultSet.close();
      preparedStatement.close();
    } catch (Exception e) {
      Log.error(e);
    } 
    
    return ret;
  }
  
  public synchronized boolean addSetingKV(String key, byte[] value) {
    if (existsSetingKey(key)) {
      return updateSetingKV(key, value);
    }
    String updateSetingSql = "INSERT INTO cacheRequest (\"requestMd5\", \"response\") VALUES (?, ?)";
    PreparedStatement preparedStatement = getPreparedStatement(updateSetingSql);
    try {
      preparedStatement.setString(1, key);
      preparedStatement.setBytes(2, value);
      int affectNum = preparedStatement.executeUpdate();
      preparedStatement.close();
      return (affectNum > 0);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } 
  }
  
  public synchronized boolean updateSetingKV(String key, byte[] value) {
    if (ApplicationContext.isOpenC("isSuperLog")) {
      Log.log(String.format("updateSetingKV key:%s value:%s", new Object[] { key, value }), new Object[0]);
    }
    if (existsSetingKey(key)) {
      String updateSetingSql = "UPDATE cacheRequest set response=? WHERE requestMd5=?";
      PreparedStatement preparedStatement = getPreparedStatement(updateSetingSql);
      try {
        preparedStatement.setBytes(1, value);
        preparedStatement.setString(2, key);
        int affectNum = preparedStatement.executeUpdate();
        preparedStatement.close();
        return (affectNum > 0);
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      } 
    } 
    return addSetingKV(key, value);
  }
  
  public byte[] getSetingValue(String key) {
    String getSetingValueSql = "SELECT response FROM cacheRequest WHERE requestMd5=?";
    try {
      PreparedStatement preparedStatement = getPreparedStatement(getSetingValueSql);
      preparedStatement.setString(1, key);
      ResultSet resultSet = preparedStatement.executeQuery();
      byte[] value = resultSet.next() ? resultSet.getBytes("response") : null;
      resultSet.close();
      preparedStatement.close();
      return value;
    } catch (Exception e) {
      Log.error(e);
      return null;
    } 
  }
  public boolean existsSetingKey(String key) {
    String selectKeyNumSql = "SELECT COUNT(1) as c FROM cacheRequest WHERE requestMd5=?";
    try {
      PreparedStatement preparedStatement = getPreparedStatement(selectKeyNumSql);
      preparedStatement.setString(1, key);
      int c = preparedStatement.executeQuery().getInt("c");
      preparedStatement.close();
      return (c > 0);
    } catch (Exception e) {
      Log.error(e);
      return false;
    } 
  }
  public PreparedStatement getPreparedStatement(String sql) {
    if (this.dbConn != null) {
      try {
        return this.dbConn.prepareStatement(sql);
      } catch (SQLException e) {
        
        Log.error(e);
        return null;
      } 
    }
    return null;
  }
  
  public Statement getStatement() {
    if (this.dbConn != null) {
      try {
        return this.dbConn.createStatement();
      } catch (SQLException e) {
        Log.error(e);
        return null;
      } 
    }
    return null;
  }
  
  public void Tclose() {
    try {
      if (this.dbConn != null && !this.dbConn.isClosed()) {
        this.dbConn.close();
      }
    } catch (SQLException e) {
      Log.error(e);
    } 
  }
}
