package org.bouncycastle.pqc.crypto.gmss.util;

public class GMSSUtil {
  public byte[] intToBytesLittleEndian(int paramInt) {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(paramInt & 0xFF);
    arrayOfByte[1] = (byte)(paramInt >> 8 & 0xFF);
    arrayOfByte[2] = (byte)(paramInt >> 16 & 0xFF);
    arrayOfByte[3] = (byte)(paramInt >> 24 & 0xFF);
    return arrayOfByte;
  }
  
  public int bytesToIntLittleEndian(byte[] paramArrayOfbyte) {
    return paramArrayOfbyte[0] & 0xFF | (paramArrayOfbyte[1] & 0xFF) << 8 | (paramArrayOfbyte[2] & 0xFF) << 16 | (paramArrayOfbyte[3] & 0xFF) << 24;
  }
  
  public int bytesToIntLittleEndian(byte[] paramArrayOfbyte, int paramInt) {
    return paramArrayOfbyte[paramInt++] & 0xFF | (paramArrayOfbyte[paramInt++] & 0xFF) << 8 | (paramArrayOfbyte[paramInt++] & 0xFF) << 16 | (paramArrayOfbyte[paramInt] & 0xFF) << 24;
  }
  
  public byte[] concatenateArray(byte[][] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length * (paramArrayOfbyte[0]).length];
    int i = 0;
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      System.arraycopy(paramArrayOfbyte[b], 0, arrayOfByte, i, (paramArrayOfbyte[b]).length);
      i += (paramArrayOfbyte[b]).length;
    } 
    return arrayOfByte;
  }
  
  public void printArray(String paramString, byte[][] paramArrayOfbyte) {
    System.out.println(paramString);
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfbyte.length; b2++) {
      for (byte b = 0; b < (paramArrayOfbyte[0]).length; b++) {
        System.out.println(b1 + "; " + paramArrayOfbyte[b2][b]);
        b1++;
      } 
    } 
  }
  
  public void printArray(String paramString, byte[] paramArrayOfbyte) {
    System.out.println(paramString);
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfbyte.length; b2++) {
      System.out.println(b1 + "; " + paramArrayOfbyte[b2]);
      b1++;
    } 
  }
  
  public boolean testPowerOfTwo(int paramInt) {
    int i;
    for (i = 1; i < paramInt; i <<= 1);
    return (paramInt == i);
  }
  
  public int getLog(int paramInt) {
    byte b = 1;
    int i = 2;
    while (i < paramInt) {
      i <<= 1;
      b++;
    } 
    return b;
  }
}
