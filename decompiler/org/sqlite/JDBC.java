package org.sqlite;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import org.sqlite.jdbc4.JDBC4Connection;












public class JDBC
  implements Driver
{
  public static final String PREFIX = "jdbc:sqlite:";
  
  static {
    try {
      DriverManager.registerDriver(new JDBC());
    }
    catch (SQLException e) {
      e.printStackTrace();
    } 
  }



  
  public int getMajorVersion() {
    return SQLiteJDBCLoader.getMajorVersion();
  }



  
  public int getMinorVersion() {
    return SQLiteJDBCLoader.getMinorVersion();
  }



  
  public boolean jdbcCompliant() {
    return false;
  }
  
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }



  
  public boolean acceptsURL(String url) {
    return isValidURL(url);
  }





  
  public static boolean isValidURL(String url) {
    return (url != null && url.toLowerCase().startsWith("jdbc:sqlite:"));
  }



  
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    return SQLiteConfig.getDriverPropertyInfo();
  }



  
  public Connection connect(String url, Properties info) throws SQLException {
    return createConnection(url, info);
  }





  
  static String extractAddress(String url) {
    return url.substring("jdbc:sqlite:".length());
  }








  
  public static SQLiteConnection createConnection(String url, Properties prop) throws SQLException {
    if (!isValidURL(url)) {
      return null;
    }
    url = url.trim();
    return (SQLiteConnection)new JDBC4Connection(url, extractAddress(url), prop);
  }
}
