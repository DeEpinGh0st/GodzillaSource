package org.sqlite;

public interface SQLiteUpdateListener
{
  void onUpdate(Type paramType, String paramString1, String paramString2, long paramLong);
  
  public enum Type
  {
    INSERT, DELETE, UPDATE;
  }
}
