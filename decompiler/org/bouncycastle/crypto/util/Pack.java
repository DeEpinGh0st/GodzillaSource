package org.bouncycastle.crypto.util;

public abstract class Pack {
  public static int bigEndianToInt(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramArrayOfbyte[paramInt] << 24;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 16;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 8;
    i |= paramArrayOfbyte[++paramInt] & 0xFF;
    return i;
  }
  
  public static void bigEndianToInt(byte[] paramArrayOfbyte, int paramInt, int[] paramArrayOfint) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      paramArrayOfint[b] = bigEndianToInt(paramArrayOfbyte, paramInt);
      paramInt += 4;
    } 
  }
  
  public static byte[] intToBigEndian(int paramInt) {
    byte[] arrayOfByte = new byte[4];
    intToBigEndian(paramInt, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void intToBigEndian(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)(paramInt1 >>> 24);
    paramArrayOfbyte[++paramInt2] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[++paramInt2] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[++paramInt2] = (byte)paramInt1;
  }
  
  public static byte[] intToBigEndian(int[] paramArrayOfint) {
    byte[] arrayOfByte = new byte[4 * paramArrayOfint.length];
    intToBigEndian(paramArrayOfint, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void intToBigEndian(int[] paramArrayOfint, byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      intToBigEndian(paramArrayOfint[b], paramArrayOfbyte, paramInt);
      paramInt += 4;
    } 
  }
  
  public static long bigEndianToLong(byte[] paramArrayOfbyte, int paramInt) {
    int i = bigEndianToInt(paramArrayOfbyte, paramInt);
    int j = bigEndianToInt(paramArrayOfbyte, paramInt + 4);
    return (i & 0xFFFFFFFFL) << 32L | j & 0xFFFFFFFFL;
  }
  
  public static void bigEndianToLong(byte[] paramArrayOfbyte, int paramInt, long[] paramArrayOflong) {
    for (byte b = 0; b < paramArrayOflong.length; b++) {
      paramArrayOflong[b] = bigEndianToLong(paramArrayOfbyte, paramInt);
      paramInt += 8;
    } 
  }
  
  public static byte[] longToBigEndian(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    longToBigEndian(paramLong, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void longToBigEndian(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    intToBigEndian((int)(paramLong >>> 32L), paramArrayOfbyte, paramInt);
    intToBigEndian((int)(paramLong & 0xFFFFFFFFL), paramArrayOfbyte, paramInt + 4);
  }
  
  public static byte[] longToBigEndian(long[] paramArrayOflong) {
    byte[] arrayOfByte = new byte[8 * paramArrayOflong.length];
    longToBigEndian(paramArrayOflong, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void longToBigEndian(long[] paramArrayOflong, byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b < paramArrayOflong.length; b++) {
      longToBigEndian(paramArrayOflong[b], paramArrayOfbyte, paramInt);
      paramInt += 8;
    } 
  }
  
  public static int littleEndianToInt(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramArrayOfbyte[paramInt] & 0xFF;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 8;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 16;
    i |= paramArrayOfbyte[++paramInt] << 24;
    return i;
  }
  
  public static void littleEndianToInt(byte[] paramArrayOfbyte, int paramInt, int[] paramArrayOfint) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      paramArrayOfint[b] = littleEndianToInt(paramArrayOfbyte, paramInt);
      paramInt += 4;
    } 
  }
  
  public static void littleEndianToInt(byte[] paramArrayOfbyte, int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    for (byte b = 0; b < paramInt3; b++) {
      paramArrayOfint[paramInt2 + b] = littleEndianToInt(paramArrayOfbyte, paramInt1);
      paramInt1 += 4;
    } 
  }
  
  public static byte[] intToLittleEndian(int paramInt) {
    byte[] arrayOfByte = new byte[4];
    intToLittleEndian(paramInt, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void intToLittleEndian(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
    paramArrayOfbyte[++paramInt2] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[++paramInt2] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[++paramInt2] = (byte)(paramInt1 >>> 24);
  }
  
  public static byte[] intToLittleEndian(int[] paramArrayOfint) {
    byte[] arrayOfByte = new byte[4 * paramArrayOfint.length];
    intToLittleEndian(paramArrayOfint, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void intToLittleEndian(int[] paramArrayOfint, byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      intToLittleEndian(paramArrayOfint[b], paramArrayOfbyte, paramInt);
      paramInt += 4;
    } 
  }
  
  public static long littleEndianToLong(byte[] paramArrayOfbyte, int paramInt) {
    int i = littleEndianToInt(paramArrayOfbyte, paramInt);
    int j = littleEndianToInt(paramArrayOfbyte, paramInt + 4);
    return (j & 0xFFFFFFFFL) << 32L | i & 0xFFFFFFFFL;
  }
  
  public static void littleEndianToLong(byte[] paramArrayOfbyte, int paramInt, long[] paramArrayOflong) {
    for (byte b = 0; b < paramArrayOflong.length; b++) {
      paramArrayOflong[b] = littleEndianToLong(paramArrayOfbyte, paramInt);
      paramInt += 8;
    } 
  }
  
  public static byte[] longToLittleEndian(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    longToLittleEndian(paramLong, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void longToLittleEndian(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    intToLittleEndian((int)(paramLong & 0xFFFFFFFFL), paramArrayOfbyte, paramInt);
    intToLittleEndian((int)(paramLong >>> 32L), paramArrayOfbyte, paramInt + 4);
  }
  
  public static byte[] longToLittleEndian(long[] paramArrayOflong) {
    byte[] arrayOfByte = new byte[8 * paramArrayOflong.length];
    longToLittleEndian(paramArrayOflong, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void longToLittleEndian(long[] paramArrayOflong, byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b < paramArrayOflong.length; b++) {
      longToLittleEndian(paramArrayOflong[b], paramArrayOfbyte, paramInt);
      paramInt += 8;
    } 
  }
}
