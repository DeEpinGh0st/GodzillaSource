package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInput;

@GwtIncompatible
public interface ByteArrayDataInput extends DataInput {
  void readFully(byte[] paramArrayOfbyte);
  
  void readFully(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  int skipBytes(int paramInt);
  
  @CanIgnoreReturnValue
  boolean readBoolean();
  
  @CanIgnoreReturnValue
  byte readByte();
  
  @CanIgnoreReturnValue
  int readUnsignedByte();
  
  @CanIgnoreReturnValue
  short readShort();
  
  @CanIgnoreReturnValue
  int readUnsignedShort();
  
  @CanIgnoreReturnValue
  char readChar();
  
  @CanIgnoreReturnValue
  int readInt();
  
  @CanIgnoreReturnValue
  long readLong();
  
  @CanIgnoreReturnValue
  float readFloat();
  
  @CanIgnoreReturnValue
  double readDouble();
  
  @CanIgnoreReturnValue
  String readLine();
  
  @CanIgnoreReturnValue
  String readUTF();
}
