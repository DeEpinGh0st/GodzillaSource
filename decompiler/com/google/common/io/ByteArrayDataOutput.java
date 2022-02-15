package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import java.io.DataOutput;

@GwtIncompatible
public interface ByteArrayDataOutput extends DataOutput {
  void write(int paramInt);
  
  void write(byte[] paramArrayOfbyte);
  
  void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  void writeBoolean(boolean paramBoolean);
  
  void writeByte(int paramInt);
  
  void writeShort(int paramInt);
  
  void writeChar(int paramInt);
  
  void writeInt(int paramInt);
  
  void writeLong(long paramLong);
  
  void writeFloat(float paramFloat);
  
  void writeDouble(double paramDouble);
  
  void writeChars(String paramString);
  
  void writeUTF(String paramString);
  
  @Deprecated
  void writeBytes(String paramString);
  
  byte[] toByteArray();
}
