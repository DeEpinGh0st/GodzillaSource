package com.kichik.pecoff4j.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;








public class DataReader
  implements IDataReader
{
  private InputStream dis;
  private int position = 0;
  
  public DataReader(byte[] buffer) {
    this.dis = new BufferedInputStream(new ByteArrayInputStream(buffer));
  }
  
  public DataReader(byte[] buffer, int offset, int length) {
    this.dis = new BufferedInputStream(new ByteArrayInputStream(buffer, offset, length));
  }

  
  public DataReader(InputStream is) {
    if (is instanceof BufferedInputStream) {
      this.dis = is;
    } else {
      this.dis = new BufferedInputStream(is);
    } 
  }

  
  public int readByte() throws IOException {
    this.position++;
    return safeRead();
  }

  
  public int readWord() throws IOException {
    this.position += 2;
    return safeRead() | safeRead() << 8;
  }

  
  public long readLong() throws IOException {
    return readDoubleWord() & 0xFFFFFFFFL | 
      readDoubleWord() << 32L;
  }

  
  public int readDoubleWord() throws IOException {
    this.position += 4;
    return safeRead() | safeRead() << 8 | safeRead() << 16 | 
      safeRead() << 24;
  }

  
  public int getPosition() {
    return this.position;
  }


  
  public boolean hasMore() throws IOException {
    return (this.dis.available() > 0);
  }

  
  public void jumpTo(int location) throws IOException {
    if (location < this.position) {
      throw new IOException("DataReader does not support scanning backwards (" + location + ")");
    }
    
    if (location > this.position) {
      skipBytes(location - this.position);
    }
  }
  
  public void skipBytes(int numBytes) throws IOException {
    this.position += numBytes;
    for (int i = 0; i < numBytes; i++) {
      safeRead();
    }
  }

  
  public void close() throws IOException {
    this.dis.close();
  }

  
  public void read(byte[] b) throws IOException {
    this.position += b.length;
    safeRead(b);
  }

  
  public String readUtf(int size) throws IOException {
    this.position += size;
    byte[] b = new byte[size];
    safeRead(b);
    int i = 0;
    for (; i < b.length && 
      b[i] != 0; i++);

    
    return new String(b, 0, i);
  }

  
  public String readUtf() throws IOException {
    StringBuilder sb = new StringBuilder();
    int c = 0;
    while ((c = readByte()) != 0) {
      if (c == -1)
        throw new IOException("Unexpected end of stream"); 
      sb.append((char)c);
    } 
    return sb.toString();
  }

  
  public String readUnicode() throws IOException {
    StringBuilder sb = new StringBuilder();
    char c = Character.MIN_VALUE;
    while ((c = (char)readWord()) != '\000') {
      sb.append(c);
    }
    if (sb.length() == 0) {
      return null;
    }
    return sb.toString();
  }

  
  public String readUnicode(int size) throws IOException {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < size; i++) {
      sb.append((char)readWord());
    }
    return sb.toString();
  }

  
  public byte[] readAll() throws IOException {
    byte[] all = new byte[this.dis.available()];
    read(all);
    return all;
  }
  
  private int safeRead() throws IOException {
    int b = this.dis.read();
    if (b == -1)
      throw new EOFException("Expected to read bytes from the stream"); 
    return b;
  }
  
  private void safeRead(byte[] b) throws IOException {
    int read = this.dis.read(b);
    if (read != b.length)
      throw new EOFException("Expected to read bytes from the stream"); 
  }
}
