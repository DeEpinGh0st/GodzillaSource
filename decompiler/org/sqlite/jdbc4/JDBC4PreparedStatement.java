package org.sqlite.jdbc4;

import java.io.InputStream;
import java.io.Reader;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.util.Arrays;
import org.sqlite.SQLiteConnection;
import org.sqlite.jdbc3.JDBC3PreparedStatement;

public class JDBC4PreparedStatement
  extends JDBC3PreparedStatement
  implements PreparedStatement, ParameterMetaData
{
  public String toString() {
    return this.sql + " \n parameters=" + Arrays.toString(this.batch);
  }
  
  public JDBC4PreparedStatement(SQLiteConnection conn, String sql) throws SQLException {
    super(conn, sql);
  }


  
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setNString(int parameterIndex, String value) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
}
