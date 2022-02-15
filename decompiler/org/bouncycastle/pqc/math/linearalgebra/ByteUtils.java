package org.bouncycastle.pqc.math.linearalgebra;

public final class ByteUtils {
  private static final char[] HEX_CHARS = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
  
  public static boolean equals(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null)
      return (paramArrayOfbyte2 == null); 
    if (paramArrayOfbyte2 == null)
      return false; 
    if (paramArrayOfbyte1.length != paramArrayOfbyte2.length)
      return false; 
    int i = 1;
    for (int j = paramArrayOfbyte1.length - 1; j >= 0; j--)
      i &= (paramArrayOfbyte1[j] == paramArrayOfbyte2[j]) ? 1 : 0; 
    return i;
  }
  
  public static boolean equals(byte[][] paramArrayOfbyte1, byte[][] paramArrayOfbyte2) {
    if (paramArrayOfbyte1.length != paramArrayOfbyte2.length)
      return false; 
    boolean bool = true;
    for (int i = paramArrayOfbyte1.length - 1; i >= 0; i--)
      bool &= equals(paramArrayOfbyte1[i], paramArrayOfbyte2[i]); 
    return bool;
  }
  
  public static boolean equals(byte[][][] paramArrayOfbyte1, byte[][][] paramArrayOfbyte2) {
    if (paramArrayOfbyte1.length != paramArrayOfbyte2.length)
      return false; 
    boolean bool = true;
    for (int i = paramArrayOfbyte1.length - 1; i >= 0; i--) {
      if ((paramArrayOfbyte1[i]).length != (paramArrayOfbyte2[i]).length)
        return false; 
      for (int j = (paramArrayOfbyte1[i]).length - 1; j >= 0; j--)
        bool &= equals(paramArrayOfbyte1[i][j], paramArrayOfbyte2[i][j]); 
    } 
    return bool;
  }
  
  public static int deepHashCode(byte[] paramArrayOfbyte) {
    int i = 1;
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      i = 31 * i + paramArrayOfbyte[b]; 
    return i;
  }
  
  public static int deepHashCode(byte[][] paramArrayOfbyte) {
    int i = 1;
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      i = 31 * i + deepHashCode(paramArrayOfbyte[b]); 
    return i;
  }
  
  public static int deepHashCode(byte[][][] paramArrayOfbyte) {
    int i = 1;
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      i = 31 * i + deepHashCode(paramArrayOfbyte[b]); 
    return i;
  }
  
  public static byte[] clone(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return null; 
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramArrayOfbyte.length);
    return arrayOfByte;
  }
  
  public static byte[] fromHexString(String paramString) {
    char[] arrayOfChar = paramString.toUpperCase().toCharArray();
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfChar.length; b2++) {
      if ((arrayOfChar[b2] >= '0' && arrayOfChar[b2] <= '9') || (arrayOfChar[b2] >= 'A' && arrayOfChar[b2] <= 'F'))
        b1++; 
    } 
    byte[] arrayOfByte = new byte[b1 + 1 >> 1];
    int i = b1 & 0x1;
    for (byte b3 = 0; b3 < arrayOfChar.length; b3++) {
      if (arrayOfChar[b3] >= '0' && arrayOfChar[b3] <= '9') {
        arrayOfByte[i >> 1] = (byte)(arrayOfByte[i >> 1] << 4);
        arrayOfByte[i >> 1] = (byte)(arrayOfByte[i >> 1] | arrayOfChar[b3] - 48);
      } else if (arrayOfChar[b3] >= 'A' && arrayOfChar[b3] <= 'F') {
        arrayOfByte[i >> 1] = (byte)(arrayOfByte[i >> 1] << 4);
        arrayOfByte[i >> 1] = (byte)(arrayOfByte[i >> 1] | arrayOfChar[b3] - 65 + 10);
      } else {
        continue;
      } 
      i++;
      continue;
    } 
    return arrayOfByte;
  }
  
  public static String toHexString(byte[] paramArrayOfbyte) {
    String str = "";
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      str = str + HEX_CHARS[paramArrayOfbyte[b] >>> 4 & 0xF];
      str = str + HEX_CHARS[paramArrayOfbyte[b] & 0xF];
    } 
    return str;
  }
  
  public static String toHexString(byte[] paramArrayOfbyte, String paramString1, String paramString2) {
    String str = new String(paramString1);
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      str = str + HEX_CHARS[paramArrayOfbyte[b] >>> 4 & 0xF];
      str = str + HEX_CHARS[paramArrayOfbyte[b] & 0xF];
      if (b < paramArrayOfbyte.length - 1)
        str = str + paramString2; 
    } 
    return str;
  }
  
  public static String toBinaryString(byte[] paramArrayOfbyte) {
    String str = "";
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      byte b1 = paramArrayOfbyte[b];
      for (byte b2 = 0; b2 < 8; b2++) {
        int i = b1 >>> b2 & 0x1;
        str = str + i;
      } 
      if (b != paramArrayOfbyte.length - 1)
        str = str + " "; 
    } 
    return str;
  }
  
  public static byte[] xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length];
    for (int i = paramArrayOfbyte1.length - 1; i >= 0; i--)
      arrayOfByte[i] = (byte)(paramArrayOfbyte1[i] ^ paramArrayOfbyte2[i]); 
    return arrayOfByte;
  }
  
  public static byte[] concatenate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte1.length);
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, paramArrayOfbyte1.length, paramArrayOfbyte2.length);
    return arrayOfByte;
  }
  
  public static byte[] concatenate(byte[][] paramArrayOfbyte) {
    int i = (paramArrayOfbyte[0]).length;
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length * i];
    int j = 0;
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      System.arraycopy(paramArrayOfbyte[b], 0, arrayOfByte, j, i);
      j += i;
    } 
    return arrayOfByte;
  }
  
  public static byte[][] split(byte[] paramArrayOfbyte, int paramInt) throws ArrayIndexOutOfBoundsException {
    if (paramInt > paramArrayOfbyte.length)
      throw new ArrayIndexOutOfBoundsException(); 
    byte[][] arrayOfByte = new byte[2][];
    arrayOfByte[0] = new byte[paramInt];
    arrayOfByte[1] = new byte[paramArrayOfbyte.length - paramInt];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte[0], 0, paramInt);
    System.arraycopy(paramArrayOfbyte, paramInt, arrayOfByte[1], 0, paramArrayOfbyte.length - paramInt);
    return arrayOfByte;
  }
  
  public static byte[] subArray(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[paramInt2 - paramInt1];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2 - paramInt1);
    return arrayOfByte;
  }
  
  public static byte[] subArray(byte[] paramArrayOfbyte, int paramInt) {
    return subArray(paramArrayOfbyte, paramInt, paramArrayOfbyte.length);
  }
  
  public static char[] toCharArray(byte[] paramArrayOfbyte) {
    char[] arrayOfChar = new char[paramArrayOfbyte.length];
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      arrayOfChar[b] = (char)paramArrayOfbyte[b]; 
    return arrayOfChar;
  }
}
