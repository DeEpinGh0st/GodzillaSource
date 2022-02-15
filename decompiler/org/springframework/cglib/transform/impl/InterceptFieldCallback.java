package org.springframework.cglib.transform.impl;

public interface InterceptFieldCallback {
  int writeInt(Object paramObject, String paramString, int paramInt1, int paramInt2);
  
  char writeChar(Object paramObject, String paramString, char paramChar1, char paramChar2);
  
  byte writeByte(Object paramObject, String paramString, byte paramByte1, byte paramByte2);
  
  boolean writeBoolean(Object paramObject, String paramString, boolean paramBoolean1, boolean paramBoolean2);
  
  short writeShort(Object paramObject, String paramString, short paramShort1, short paramShort2);
  
  float writeFloat(Object paramObject, String paramString, float paramFloat1, float paramFloat2);
  
  double writeDouble(Object paramObject, String paramString, double paramDouble1, double paramDouble2);
  
  long writeLong(Object paramObject, String paramString, long paramLong1, long paramLong2);
  
  Object writeObject(Object paramObject1, String paramString, Object paramObject2, Object paramObject3);
  
  int readInt(Object paramObject, String paramString, int paramInt);
  
  char readChar(Object paramObject, String paramString, char paramChar);
  
  byte readByte(Object paramObject, String paramString, byte paramByte);
  
  boolean readBoolean(Object paramObject, String paramString, boolean paramBoolean);
  
  short readShort(Object paramObject, String paramString, short paramShort);
  
  float readFloat(Object paramObject, String paramString, float paramFloat);
  
  double readDouble(Object paramObject, String paramString, double paramDouble);
  
  long readLong(Object paramObject, String paramString, long paramLong);
  
  Object readObject(Object paramObject1, String paramString, Object paramObject2);
}
