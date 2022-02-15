package com.kichik.pecoff4j.io;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;








public class ByteArrayDataReader
  implements IDataReader
{
  private byte[] data;
  private int position;
  private int offset;
  private int length;
  
  public ByteArrayDataReader(byte[] data) {
    this.data = data;
    this.length = data.length;
  }
  
  public ByteArrayDataReader(byte[] data, int offset, int length) {
    this.data = data;
    this.offset = offset;
    this.length = length;
    
    if (this.length + this.offset > data.length) {
      throw new IndexOutOfBoundsException("length [" + length + "] + offset [" + offset + "] > data.length [" + data.length + "]");
    }
  }


  
  public void close() throws IOException {}


  
  public int getPosition() {
    return this.position;
  }


  
  public boolean hasMore() throws IOException {
    return (this.position < this.length);
  }

  
  public void jumpTo(int location) throws IOException {
    this.position = location;
  }

  
  public void read(byte[] b) throws IOException {
    for (int i = 0; i < b.length; i++) {
      b[i] = this.data[this.offset + this.position + i];
    }
    this.position += b.length;
  }

  
  public int readByte() throws IOException {
    if (!hasMore())
      throw new EOFException("End of stream"); 
    return (char)(this.data[this.offset + this.position++] & 0xFF);
  }

  
  public long readLong() throws IOException {
    return readDoubleWord() | readDoubleWord() << 32L;
  }

  
  public int readDoubleWord() throws IOException {
    return readWord() | readWord() << 16;
  }

  
  public String readUtf(int size) throws IOException {
    byte[] b = new byte[size];
    read(b);
    return new String(b);
  }

  
  public String readUtf() throws IOException {
    StringBuilder sb = new StringBuilder();
    while (true) {
      char c = (char)readByte();
      if (c == '\000') {
        break;
      }
      sb.append(c);
    } 
    return sb.toString();
  }

  
  public int readWord() throws IOException {
    return readByte() | readByte() << 8;
  }

  
  public void skipBytes(int numBytes) throws IOException {
    this.position += numBytes;
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
    byte[] result = Arrays.copyOfRange(this.data, this.offset + this.position, this.offset + this.length);
    
    this.position = this.length;
    return result;
  }
}
