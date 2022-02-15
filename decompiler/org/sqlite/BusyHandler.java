package org.sqlite;

import java.sql.Connection;
import java.sql.SQLException;













public abstract class BusyHandler
{
  private static void commitHandler(Connection conn, BusyHandler busyHandler) throws SQLException {
    if (conn == null || !(conn instanceof SQLiteConnection)) {
      throw new SQLException("connection must be to an SQLite db");
    }
    
    if (conn.isClosed()) {
      throw new SQLException("connection closed");
    }
    
    SQLiteConnection sqliteConnection = (SQLiteConnection)conn;
    sqliteConnection.getDatabase().busy_handler(busyHandler);
  }







  
  public static final void setHandler(Connection conn, BusyHandler busyHandler) throws SQLException {
    commitHandler(conn, busyHandler);
  }






  
  public static final void clearHandler(Connection conn) throws SQLException {
    commitHandler(conn, null);
  }
  
  protected abstract int callback(int paramInt) throws SQLException;
}
