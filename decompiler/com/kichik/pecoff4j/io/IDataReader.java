package com.kichik.pecoff4j.io;

import java.io.IOException;

public interface IDataReader extends AutoCloseable {
  int readByte() throws IOException;
  
  int readWord() throws IOException;
  
  int readDoubleWord() throws IOException;
  
  long readLong() throws IOException;
  
  int getPosition();
  
  boolean hasMore() throws IOException;
  
  void jumpTo(int paramInt) throws IOException;
  
  void skipBytes(int paramInt) throws IOException;
  
  void close() throws IOException;
  
  void read(byte[] paramArrayOfbyte) throws IOException;
  
  String readUtf(int paramInt) throws IOException;
  
  String readUtf() throws IOException;
  
  String readUnicode() throws IOException;
  
  String readUnicode(int paramInt) throws IOException;
  
  byte[] readAll() throws IOException;
}
