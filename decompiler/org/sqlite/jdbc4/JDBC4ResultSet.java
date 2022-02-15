package org.sqlite.jdbc4;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import org.sqlite.core.CoreStatement;
import org.sqlite.jdbc3.JDBC3ResultSet;

public class JDBC4ResultSet
  extends JDBC3ResultSet
  implements ResultSet, ResultSetMetaData {
  public JDBC4ResultSet(CoreStatement stmt) {
    super(stmt);
  }

  
  public void close() throws SQLException {
    boolean wasOpen = isOpen();
    super.close();
    
    if (wasOpen && this.stmt instanceof JDBC4Statement) {
      JDBC4Statement stat = (JDBC4Statement)this.stmt;
      
      if (stat.closeOnCompletion && !stat.isClosed()) {
        stat.close();
      }
    } 
  }

  
  public <T> T unwrap(Class<T> iface) throws ClassCastException {
    return iface.cast(this);
  }
  
  public boolean isWrapperFor(Class<?> iface) {
    return iface.isInstance(this);
  }

  
  public RowId getRowId(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public RowId getRowId(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public void updateRowId(String columnLabel, RowId x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public int getHoldability() throws SQLException {
    return 0;
  }
  
  public boolean isClosed() throws SQLException {
    return !isOpen();
  }


  
  public void updateNString(int columnIndex, String nString) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateNString(String columnLabel, String nString) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public NClob getNClob(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public NClob getNClob(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public String getNString(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public String getNString(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  
  public Reader getNCharacterStream(int col) throws SQLException {
    String data = getString(col);
    return getNCharacterStreamInternal(data);
  }
  
  private Reader getNCharacterStreamInternal(String data) {
    if (data == null) {
      return null;
    }
    Reader reader = new StringReader(data);
    return reader;
  }
  
  public Reader getNCharacterStream(String col) throws SQLException {
    String data = getString(col);
    return getNCharacterStreamInternal(data);
  }


  
  public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  
  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  
  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  
  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  
  protected SQLException unused() {
    return new SQLFeatureNotSupportedException();
  }



  
  public Array getArray(int i) throws SQLException {
    throw unused();
  } public Array getArray(String col) throws SQLException {
    throw unused();
  }
  public InputStream getAsciiStream(int col) throws SQLException {
    String data = getString(col);
    return getAsciiStreamInternal(data);
  }
  
  public InputStream getAsciiStream(String col) throws SQLException {
    String data = getString(col);
    return getAsciiStreamInternal(data);
  }
  private InputStream getAsciiStreamInternal(String data) {
    InputStream inputStream;
    if (data == null) {
      return null;
    }
    
    try {
      inputStream = new ByteArrayInputStream(data.getBytes("ASCII"));
    } catch (UnsupportedEncodingException e) {
      return null;
    } 
    return inputStream;
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(int col, int s) throws SQLException {
    throw unused();
  } @Deprecated
  public BigDecimal getBigDecimal(String col, int s) throws SQLException {
    throw unused();
  } public Blob getBlob(int col) throws SQLException {
    throw unused();
  } public Blob getBlob(String col) throws SQLException {
    throw unused();
  }
  public Clob getClob(int col) throws SQLException {
    return new SqliteClob(getString(col));
  }
  
  public Clob getClob(String col) throws SQLException {
    return new SqliteClob(getString(col));
  }

  
  public Object getObject(int col, Map map) throws SQLException {
    throw unused();
  }
  public Object getObject(String col, Map map) throws SQLException {
    throw unused();
  } public Ref getRef(int i) throws SQLException {
    throw unused();
  } public Ref getRef(String col) throws SQLException {
    throw unused();
  }
  public InputStream getUnicodeStream(int col) throws SQLException {
    return getAsciiStream(col);
  }
  
  public InputStream getUnicodeStream(String col) throws SQLException {
    return getAsciiStream(col);
  }
  
  public URL getURL(int col) throws SQLException {
    throw unused();
  } public URL getURL(String col) throws SQLException {
    throw unused();
  }
  public void insertRow() throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public void moveToCurrentRow() throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public void moveToInsertRow() throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public boolean last() throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public boolean previous() throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public boolean relative(int rows) throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public boolean absolute(int row) throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public void afterLast() throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public void beforeFirst() throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  } public boolean first() throws SQLException {
    throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
  }
  public void cancelRowUpdates() throws SQLException {
    throw unused();
  } public void deleteRow() throws SQLException {
    throw unused();
  }
  public void updateArray(int col, Array x) throws SQLException {
    throw unused();
  } public void updateArray(String col, Array x) throws SQLException {
    throw unused();
  } public void updateAsciiStream(int col, InputStream x, int l) throws SQLException {
    throw unused();
  } public void updateAsciiStream(String col, InputStream x, int l) throws SQLException {
    throw unused();
  } public void updateBigDecimal(int col, BigDecimal x) throws SQLException {
    throw unused();
  } public void updateBigDecimal(String col, BigDecimal x) throws SQLException {
    throw unused();
  } public void updateBinaryStream(int c, InputStream x, int l) throws SQLException {
    throw unused();
  } public void updateBinaryStream(String c, InputStream x, int l) throws SQLException {
    throw unused();
  } public void updateBlob(int col, Blob x) throws SQLException {
    throw unused();
  } public void updateBlob(String col, Blob x) throws SQLException {
    throw unused();
  } public void updateBoolean(int col, boolean x) throws SQLException {
    throw unused();
  } public void updateBoolean(String col, boolean x) throws SQLException {
    throw unused();
  } public void updateByte(int col, byte x) throws SQLException {
    throw unused();
  } public void updateByte(String col, byte x) throws SQLException {
    throw unused();
  } public void updateBytes(int col, byte[] x) throws SQLException {
    throw unused();
  } public void updateBytes(String col, byte[] x) throws SQLException {
    throw unused();
  } public void updateCharacterStream(int c, Reader x, int l) throws SQLException {
    throw unused();
  } public void updateCharacterStream(String c, Reader r, int l) throws SQLException {
    throw unused();
  } public void updateClob(int col, Clob x) throws SQLException {
    throw unused();
  } public void updateClob(String col, Clob x) throws SQLException {
    throw unused();
  } public void updateDate(int col, Date x) throws SQLException {
    throw unused();
  } public void updateDate(String col, Date x) throws SQLException {
    throw unused();
  } public void updateDouble(int col, double x) throws SQLException {
    throw unused();
  } public void updateDouble(String col, double x) throws SQLException {
    throw unused();
  } public void updateFloat(int col, float x) throws SQLException {
    throw unused();
  } public void updateFloat(String col, float x) throws SQLException {
    throw unused();
  } public void updateInt(int col, int x) throws SQLException {
    throw unused();
  } public void updateInt(String col, int x) throws SQLException {
    throw unused();
  } public void updateLong(int col, long x) throws SQLException {
    throw unused();
  } public void updateLong(String col, long x) throws SQLException {
    throw unused();
  } public void updateNull(int col) throws SQLException {
    throw unused();
  } public void updateNull(String col) throws SQLException {
    throw unused();
  } public void updateObject(int c, Object x) throws SQLException {
    throw unused();
  } public void updateObject(int c, Object x, int s) throws SQLException {
    throw unused();
  } public void updateObject(String col, Object x) throws SQLException {
    throw unused();
  } public void updateObject(String c, Object x, int s) throws SQLException {
    throw unused();
  } public void updateRef(int col, Ref x) throws SQLException {
    throw unused();
  } public void updateRef(String c, Ref x) throws SQLException {
    throw unused();
  } public void updateRow() throws SQLException {
    throw unused();
  } public void updateShort(int c, short x) throws SQLException {
    throw unused();
  } public void updateShort(String c, short x) throws SQLException {
    throw unused();
  } public void updateString(int c, String x) throws SQLException {
    throw unused();
  } public void updateString(String c, String x) throws SQLException {
    throw unused();
  } public void updateTime(int c, Time x) throws SQLException {
    throw unused();
  } public void updateTime(String c, Time x) throws SQLException {
    throw unused();
  } public void updateTimestamp(int c, Timestamp x) throws SQLException {
    throw unused();
  } public void updateTimestamp(String c, Timestamp x) throws SQLException {
    throw unused();
  }
  public void refreshRow() throws SQLException {
    throw unused();
  }
  
  class SqliteClob
    implements NClob {
    private String data;
    
    protected SqliteClob(String data) {
      this.data = data;
    }
    
    public void free() throws SQLException {
      this.data = null;
    }
    
    public InputStream getAsciiStream() throws SQLException {
      return JDBC4ResultSet.this.getAsciiStreamInternal(this.data);
    }
    
    public Reader getCharacterStream() throws SQLException {
      return JDBC4ResultSet.this.getNCharacterStreamInternal(this.data);
    }
    
    public Reader getCharacterStream(long arg0, long arg1) throws SQLException {
      return JDBC4ResultSet.this.getNCharacterStreamInternal(this.data);
    }
    
    public String getSubString(long position, int length) throws SQLException {
      if (this.data == null) {
        throw new SQLException("no data");
      }
      if (position < 1L) {
        throw new SQLException("Position must be greater than or equal to 1");
      }
      if (length < 0) {
        throw new SQLException("Length must be greater than or equal to 0");
      }
      int start = (int)position - 1;
      return this.data.substring(start, Math.min(start + length, this.data.length()));
    }
    
    public long length() throws SQLException {
      if (this.data == null) {
        throw new SQLException("no data");
      }
      return this.data.length();
    }
    
    public long position(String arg0, long arg1) throws SQLException {
      JDBC4ResultSet.this.unused();
      return -1L;
    }
    
    public long position(Clob arg0, long arg1) throws SQLException {
      JDBC4ResultSet.this.unused();
      return -1L;
    }
    
    public OutputStream setAsciiStream(long arg0) throws SQLException {
      JDBC4ResultSet.this.unused();
      return null;
    }
    
    public Writer setCharacterStream(long arg0) throws SQLException {
      JDBC4ResultSet.this.unused();
      return null;
    }
    
    public int setString(long arg0, String arg1) throws SQLException {
      JDBC4ResultSet.this.unused();
      return -1;
    }
    
    public int setString(long arg0, String arg1, int arg2, int arg3) throws SQLException {
      JDBC4ResultSet.this.unused();
      return -1;
    }
    
    public void truncate(long arg0) throws SQLException {
      JDBC4ResultSet.this.unused();
    }
  }
}
