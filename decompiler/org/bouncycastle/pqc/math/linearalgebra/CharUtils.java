package org.bouncycastle.pqc.math.linearalgebra;

public final class CharUtils {
  public static char[] clone(char[] paramArrayOfchar) {
    char[] arrayOfChar = new char[paramArrayOfchar.length];
    System.arraycopy(paramArrayOfchar, 0, arrayOfChar, 0, paramArrayOfchar.length);
    return arrayOfChar;
  }
  
  public static byte[] toByteArray(char[] paramArrayOfchar) {
    byte[] arrayOfByte = new byte[paramArrayOfchar.length];
    for (int i = paramArrayOfchar.length - 1; i >= 0; i--)
      arrayOfByte[i] = (byte)paramArrayOfchar[i]; 
    return arrayOfByte;
  }
  
  public static byte[] toByteArrayForPBE(char[] paramArrayOfchar) {
    byte[] arrayOfByte1 = new byte[paramArrayOfchar.length];
    int i;
    for (i = 0; i < paramArrayOfchar.length; i++)
      arrayOfByte1[i] = (byte)paramArrayOfchar[i]; 
    i = arrayOfByte1.length * 2;
    byte[] arrayOfByte2 = new byte[i + 2];
    int j = 0;
    for (byte b = 0; b < arrayOfByte1.length; b++) {
      j = b * 2;
      arrayOfByte2[j] = 0;
      arrayOfByte2[j + 1] = arrayOfByte1[b];
    } 
    arrayOfByte2[i] = 0;
    arrayOfByte2[i + 1] = 0;
    return arrayOfByte2;
  }
  
  public static boolean equals(char[] paramArrayOfchar1, char[] paramArrayOfchar2) {
    if (paramArrayOfchar1.length != paramArrayOfchar2.length)
      return false; 
    int i = 1;
    for (int j = paramArrayOfchar1.length - 1; j >= 0; j--)
      i &= (paramArrayOfchar1[j] == paramArrayOfchar2[j]) ? 1 : 0; 
    return i;
  }
}
