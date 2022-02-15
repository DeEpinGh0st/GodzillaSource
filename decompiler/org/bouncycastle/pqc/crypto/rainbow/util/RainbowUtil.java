package org.bouncycastle.pqc.crypto.rainbow.util;

public class RainbowUtil {
  public static int[] convertArraytoInt(byte[] paramArrayOfbyte) {
    int[] arrayOfInt = new int[paramArrayOfbyte.length];
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      arrayOfInt[b] = paramArrayOfbyte[b] & 0xFF; 
    return arrayOfInt;
  }
  
  public static short[] convertArray(byte[] paramArrayOfbyte) {
    short[] arrayOfShort = new short[paramArrayOfbyte.length];
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      arrayOfShort[b] = (short)(paramArrayOfbyte[b] & 0xFF); 
    return arrayOfShort;
  }
  
  public static short[][] convertArray(byte[][] paramArrayOfbyte) {
    short[][] arrayOfShort = new short[paramArrayOfbyte.length][(paramArrayOfbyte[0]).length];
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfbyte[0]).length; b1++)
        arrayOfShort[b][b1] = (short)(paramArrayOfbyte[b][b1] & 0xFF); 
    } 
    return arrayOfShort;
  }
  
  public static short[][][] convertArray(byte[][][] paramArrayOfbyte) {
    short[][][] arrayOfShort = new short[paramArrayOfbyte.length][(paramArrayOfbyte[0]).length][(paramArrayOfbyte[0][0]).length];
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfbyte[0]).length; b1++) {
        for (byte b2 = 0; b2 < (paramArrayOfbyte[0][0]).length; b2++)
          arrayOfShort[b][b1][b2] = (short)(paramArrayOfbyte[b][b1][b2] & 0xFF); 
      } 
    } 
    return arrayOfShort;
  }
  
  public static byte[] convertIntArray(int[] paramArrayOfint) {
    byte[] arrayOfByte = new byte[paramArrayOfint.length];
    for (byte b = 0; b < paramArrayOfint.length; b++)
      arrayOfByte[b] = (byte)paramArrayOfint[b]; 
    return arrayOfByte;
  }
  
  public static byte[] convertArray(short[] paramArrayOfshort) {
    byte[] arrayOfByte = new byte[paramArrayOfshort.length];
    for (byte b = 0; b < paramArrayOfshort.length; b++)
      arrayOfByte[b] = (byte)paramArrayOfshort[b]; 
    return arrayOfByte;
  }
  
  public static byte[][] convertArray(short[][] paramArrayOfshort) {
    byte[][] arrayOfByte = new byte[paramArrayOfshort.length][(paramArrayOfshort[0]).length];
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfshort[0]).length; b1++)
        arrayOfByte[b][b1] = (byte)paramArrayOfshort[b][b1]; 
    } 
    return arrayOfByte;
  }
  
  public static byte[][][] convertArray(short[][][] paramArrayOfshort) {
    byte[][][] arrayOfByte = new byte[paramArrayOfshort.length][(paramArrayOfshort[0]).length][(paramArrayOfshort[0][0]).length];
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfshort[0]).length; b1++) {
        for (byte b2 = 0; b2 < (paramArrayOfshort[0][0]).length; b2++)
          arrayOfByte[b][b1][b2] = (byte)paramArrayOfshort[b][b1][b2]; 
      } 
    } 
    return arrayOfByte;
  }
  
  public static boolean equals(short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    if (paramArrayOfshort1.length != paramArrayOfshort2.length)
      return false; 
    int i = 1;
    for (int j = paramArrayOfshort1.length - 1; j >= 0; j--)
      i &= (paramArrayOfshort1[j] == paramArrayOfshort2[j]) ? 1 : 0; 
    return i;
  }
  
  public static boolean equals(short[][] paramArrayOfshort1, short[][] paramArrayOfshort2) {
    if (paramArrayOfshort1.length != paramArrayOfshort2.length)
      return false; 
    boolean bool = true;
    for (int i = paramArrayOfshort1.length - 1; i >= 0; i--)
      bool &= equals(paramArrayOfshort1[i], paramArrayOfshort2[i]); 
    return bool;
  }
  
  public static boolean equals(short[][][] paramArrayOfshort1, short[][][] paramArrayOfshort2) {
    if (paramArrayOfshort1.length != paramArrayOfshort2.length)
      return false; 
    boolean bool = true;
    for (int i = paramArrayOfshort1.length - 1; i >= 0; i--)
      bool &= equals(paramArrayOfshort1[i], paramArrayOfshort2[i]); 
    return bool;
  }
}
