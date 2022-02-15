package org.sqlite.javax;

import java.sql.SQLException;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;




















public class SQLiteConnectionPoolDataSource
  extends SQLiteDataSource
  implements ConnectionPoolDataSource
{
  public SQLiteConnectionPoolDataSource() {}
  
  public SQLiteConnectionPoolDataSource(SQLiteConfig config) {
    super(config);
  }



  
  public PooledConnection getPooledConnection() throws SQLException {
    return getPooledConnection(null, null);
  }



  
  public PooledConnection getPooledConnection(String user, String password) throws SQLException {
    return (PooledConnection)new SQLitePooledConnection(getConnection(user, password));
  }
}
