package org.bouncycastle.pqc.math.linearalgebra;

public final class LittleEndianConversions {
  public static int OS2IP(byte[] paramArrayOfbyte) {
    return paramArrayOfbyte[0] & 0xFF | (paramArrayOfbyte[1] & 0xFF) << 8 | (paramArrayOfbyte[2] & 0xFF) << 16 | (paramArrayOfbyte[3] & 0xFF) << 24;
  }
  
  public static int OS2IP(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramArrayOfbyte[paramInt++] & 0xFF;
    i |= (paramArrayOfbyte[paramInt++] & 0xFF) << 8;
    i |= (paramArrayOfbyte[paramInt++] & 0xFF) << 16;
    i |= (paramArrayOfbyte[paramInt] & 0xFF) << 24;
    return i;
  }
  
  public static int OS2IP(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = 0;
    for (int j = paramInt2 - 1; j >= 0; j--)
      i |= (paramArrayOfbyte[paramInt1 + j] & 0xFF) << 8 * j; 
    return i;
  }
  
  public static long OS2LIP(byte[] paramArrayOfbyte, int paramInt) {
    long l = (paramArrayOfbyte[paramInt++] & 0xFF);
    l |= ((paramArrayOfbyte[paramInt++] & 0xFF) << 8);
    l |= ((paramArrayOfbyte[paramInt++] & 0xFF) << 16);
    l |= (paramArrayOfbyte[paramInt++] & 0xFFL) << 24L;
    l |= (paramArrayOfbyte[paramInt++] & 0xFFL) << 32L;
    l |= (paramArrayOfbyte[paramInt++] & 0xFFL) << 40L;
    l |= (paramArrayOfbyte[paramInt++] & 0xFFL) << 48L;
    l |= (paramArrayOfbyte[paramInt++] & 0xFFL) << 56L;
    return l;
  }
  
  public static byte[] I2OSP(int paramInt) {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)paramInt;
    arrayOfByte[1] = (byte)(paramInt >>> 8);
    arrayOfByte[2] = (byte)(paramInt >>> 16);
    arrayOfByte[3] = (byte)(paramInt >>> 24);
    return arrayOfByte;
  }
  
  public static void I2OSP(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2++] = (byte)paramInt1;
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 24);
  }
  
  public static void I2OSP(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    for (int i = paramInt3 - 1; i >= 0; i--)
      paramArrayOfbyte[paramInt2 + i] = (byte)(paramInt1 >>> 8 * i); 
  }
  
  public static byte[] I2OSP(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    arrayOfByte[0] = (byte)(int)paramLong;
    arrayOfByte[1] = (byte)(int)(paramLong >>> 8L);
    arrayOfByte[2] = (byte)(int)(paramLong >>> 16L);
    arrayOfByte[3] = (byte)(int)(paramLong >>> 24L);
    arrayOfByte[4] = (byte)(int)(paramLong >>> 32L);
    arrayOfByte[5] = (byte)(int)(paramLong >>> 40L);
    arrayOfByte[6] = (byte)(int)(paramLong >>> 48L);
    arrayOfByte[7] = (byte)(int)(paramLong >>> 56L);
    return arrayOfByte;
  }
  
  public static void I2OSP(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    paramArrayOfbyte[paramInt++] = (byte)(int)paramLong;
    paramArrayOfbyte[paramInt++] = (byte)(int)(paramLong >>> 8L);
    paramArrayOfbyte[paramInt++] = (byte)(int)(paramLong >>> 16L);
    paramArrayOfbyte[paramInt++] = (byte)(int)(paramLong >>> 24L);
    paramArrayOfbyte[paramInt++] = (byte)(int)(paramLong >>> 32L);
    paramArrayOfbyte[paramInt++] = (byte)(int)(paramLong >>> 40L);
    paramArrayOfbyte[paramInt++] = (byte)(int)(paramLong >>> 48L);
    paramArrayOfbyte[paramInt] = (byte)(int)(paramLong >>> 56L);
  }
  
  public static byte[] toByteArray(int[] paramArrayOfint, int paramInt) {
    int i = paramArrayOfint.length;
    byte[] arrayOfByte = new byte[paramInt];
    byte b1 = 0;
    byte b2 = 0;
    while (b2 <= i - 2) {
      I2OSP(paramArrayOfint[b2], arrayOfByte, b1);
      b2++;
      b1 += true;
    } 
    I2OSP(paramArrayOfint[i - 1], arrayOfByte, b1, paramInt - b1);
    return arrayOfByte;
  }
  
  public static int[] toIntArray(byte[] paramArrayOfbyte) {
    int i = (paramArrayOfbyte.length + 3) / 4;
    int j = paramArrayOfbyte.length & 0x3;
    int[] arrayOfInt = new int[i];
    boolean bool = false;
    byte b = 0;
    while (b <= i - 2) {
      arrayOfInt[b] = OS2IP(paramArrayOfbyte, bool);
      b++;
      bool += true;
    } 
    if (j != 0) {
      arrayOfInt[i - 1] = OS2IP(paramArrayOfbyte, bool, j);
    } else {
      arrayOfInt[i - 1] = OS2IP(paramArrayOfbyte, bool);
    } 
    return arrayOfInt;
  }
}
