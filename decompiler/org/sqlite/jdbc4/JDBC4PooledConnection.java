package org.sqlite.jdbc4;

import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

public abstract class JDBC4PooledConnection implements PooledConnection {
  public void addStatementEventListener(StatementEventListener listener) {}
  
  public void removeStatementEventListener(StatementEventListener listener) {}
}
