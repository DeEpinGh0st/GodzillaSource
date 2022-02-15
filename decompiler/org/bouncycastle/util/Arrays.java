package org.bouncycastle.util;

import java.math.BigInteger;
import java.util.NoSuchElementException;

public final class Arrays {
  public static boolean areEqual(boolean[] paramArrayOfboolean1, boolean[] paramArrayOfboolean2) {
    if (paramArrayOfboolean1 == paramArrayOfboolean2)
      return true; 
    if (paramArrayOfboolean1 == null || paramArrayOfboolean2 == null)
      return false; 
    if (paramArrayOfboolean1.length != paramArrayOfboolean2.length)
      return false; 
    for (byte b = 0; b != paramArrayOfboolean1.length; b++) {
      if (paramArrayOfboolean1[b] != paramArrayOfboolean2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean areEqual(char[] paramArrayOfchar1, char[] paramArrayOfchar2) {
    if (paramArrayOfchar1 == paramArrayOfchar2)
      return true; 
    if (paramArrayOfchar1 == null || paramArrayOfchar2 == null)
      return false; 
    if (paramArrayOfchar1.length != paramArrayOfchar2.length)
      return false; 
    for (byte b = 0; b != paramArrayOfchar1.length; b++) {
      if (paramArrayOfchar1[b] != paramArrayOfchar2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean areEqual(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == paramArrayOfbyte2)
      return true; 
    if (paramArrayOfbyte1 == null || paramArrayOfbyte2 == null)
      return false; 
    if (paramArrayOfbyte1.length != paramArrayOfbyte2.length)
      return false; 
    for (byte b = 0; b != paramArrayOfbyte1.length; b++) {
      if (paramArrayOfbyte1[b] != paramArrayOfbyte2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean areEqual(short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    if (paramArrayOfshort1 == paramArrayOfshort2)
      return true; 
    if (paramArrayOfshort1 == null || paramArrayOfshort2 == null)
      return false; 
    if (paramArrayOfshort1.length != paramArrayOfshort2.length)
      return false; 
    for (byte b = 0; b != paramArrayOfshort1.length; b++) {
      if (paramArrayOfshort1[b] != paramArrayOfshort2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean constantTimeAreEqual(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == paramArrayOfbyte2)
      return true; 
    if (paramArrayOfbyte1 == null || paramArrayOfbyte2 == null)
      return false; 
    if (paramArrayOfbyte1.length != paramArrayOfbyte2.length)
      return !constantTimeAreEqual(paramArrayOfbyte1, paramArrayOfbyte1); 
    int i = 0;
    for (byte b = 0; b != paramArrayOfbyte1.length; b++)
      i |= paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]; 
    return (i == 0);
  }
  
  public static boolean areEqual(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (paramArrayOfint1 == paramArrayOfint2)
      return true; 
    if (paramArrayOfint1 == null || paramArrayOfint2 == null)
      return false; 
    if (paramArrayOfint1.length != paramArrayOfint2.length)
      return false; 
    for (byte b = 0; b != paramArrayOfint1.length; b++) {
      if (paramArrayOfint1[b] != paramArrayOfint2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean areEqual(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    if (paramArrayOflong1 == paramArrayOflong2)
      return true; 
    if (paramArrayOflong1 == null || paramArrayOflong2 == null)
      return false; 
    if (paramArrayOflong1.length != paramArrayOflong2.length)
      return false; 
    for (byte b = 0; b != paramArrayOflong1.length; b++) {
      if (paramArrayOflong1[b] != paramArrayOflong2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean areEqual(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) {
    if (paramArrayOfObject1 == paramArrayOfObject2)
      return true; 
    if (paramArrayOfObject1 == null || paramArrayOfObject2 == null)
      return false; 
    if (paramArrayOfObject1.length != paramArrayOfObject2.length)
      return false; 
    for (byte b = 0; b != paramArrayOfObject1.length; b++) {
      Object object1 = paramArrayOfObject1[b];
      Object object2 = paramArrayOfObject2[b];
      if (object1 == null) {
        if (object2 != null)
          return false; 
      } else if (!object1.equals(object2)) {
        return false;
      } 
    } 
    return true;
  }
  
  public static int compareUnsigned(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == paramArrayOfbyte2)
      return 0; 
    if (paramArrayOfbyte1 == null)
      return -1; 
    if (paramArrayOfbyte2 == null)
      return 1; 
    int i = Math.min(paramArrayOfbyte1.length, paramArrayOfbyte2.length);
    for (byte b = 0; b < i; b++) {
      int j = paramArrayOfbyte1[b] & 0xFF;
      int k = paramArrayOfbyte2[b] & 0xFF;
      if (j < k)
        return -1; 
      if (j > k)
        return 1; 
    } 
    return (paramArrayOfbyte1.length < paramArrayOfbyte2.length) ? -1 : ((paramArrayOfbyte1.length > paramArrayOfbyte2.length) ? 1 : 0);
  }
  
  public static boolean contains(short[] paramArrayOfshort, short paramShort) {
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      if (paramArrayOfshort[b] == paramShort)
        return true; 
    } 
    return false;
  }
  
  public static boolean contains(int[] paramArrayOfint, int paramInt) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      if (paramArrayOfint[b] == paramInt)
        return true; 
    } 
    return false;
  }
  
  public static void fill(byte[] paramArrayOfbyte, byte paramByte) {
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      paramArrayOfbyte[b] = paramByte; 
  }
  
  public static void fill(char[] paramArrayOfchar, char paramChar) {
    for (byte b = 0; b < paramArrayOfchar.length; b++)
      paramArrayOfchar[b] = paramChar; 
  }
  
  public static void fill(long[] paramArrayOflong, long paramLong) {
    for (byte b = 0; b < paramArrayOflong.length; b++)
      paramArrayOflong[b] = paramLong; 
  }
  
  public static void fill(short[] paramArrayOfshort, short paramShort) {
    for (byte b = 0; b < paramArrayOfshort.length; b++)
      paramArrayOfshort[b] = paramShort; 
  }
  
  public static void fill(int[] paramArrayOfint, int paramInt) {
    for (byte b = 0; b < paramArrayOfint.length; b++)
      paramArrayOfint[b] = paramInt; 
  }
  
  public static int hashCode(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return 0; 
    int i = paramArrayOfbyte.length;
    int j;
    for (j = i + 1; --i >= 0; j ^= paramArrayOfbyte[i])
      j *= 257; 
    return j;
  }
  
  public static int hashCode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramArrayOfbyte == null)
      return 0; 
    int i = paramInt2;
    int j;
    for (j = i + 1; --i >= 0; j ^= paramArrayOfbyte[paramInt1 + i])
      j *= 257; 
    return j;
  }
  
  public static int hashCode(char[] paramArrayOfchar) {
    if (paramArrayOfchar == null)
      return 0; 
    int i = paramArrayOfchar.length;
    int j;
    for (j = i + 1; --i >= 0; j ^= paramArrayOfchar[i])
      j *= 257; 
    return j;
  }
  
  public static int hashCode(int[][] paramArrayOfint) {
    int i = 0;
    for (byte b = 0; b != paramArrayOfint.length; b++)
      i = i * 257 + hashCode(paramArrayOfint[b]); 
    return i;
  }
  
  public static int hashCode(int[] paramArrayOfint) {
    if (paramArrayOfint == null)
      return 0; 
    int i = paramArrayOfint.length;
    int j;
    for (j = i + 1; --i >= 0; j ^= paramArrayOfint[i])
      j *= 257; 
    return j;
  }
  
  public static int hashCode(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    if (paramArrayOfint == null)
      return 0; 
    int i = paramInt2;
    int j;
    for (j = i + 1; --i >= 0; j ^= paramArrayOfint[paramInt1 + i])
      j *= 257; 
    return j;
  }
  
  public static int hashCode(long[] paramArrayOflong) {
    if (paramArrayOflong == null)
      return 0; 
    int i = paramArrayOflong.length;
    int j;
    for (j = i + 1; --i >= 0; j ^= (int)(l >>> 32L)) {
      long l = paramArrayOflong[i];
      j *= 257;
      j ^= (int)l;
      j *= 257;
    } 
    return j;
  }
  
  public static int hashCode(long[] paramArrayOflong, int paramInt1, int paramInt2) {
    if (paramArrayOflong == null)
      return 0; 
    int i = paramInt2;
    int j;
    for (j = i + 1; --i >= 0; j ^= (int)(l >>> 32L)) {
      long l = paramArrayOflong[paramInt1 + i];
      j *= 257;
      j ^= (int)l;
      j *= 257;
    } 
    return j;
  }
  
  public static int hashCode(short[][][] paramArrayOfshort) {
    int i = 0;
    for (byte b = 0; b != paramArrayOfshort.length; b++)
      i = i * 257 + hashCode(paramArrayOfshort[b]); 
    return i;
  }
  
  public static int hashCode(short[][] paramArrayOfshort) {
    int i = 0;
    for (byte b = 0; b != paramArrayOfshort.length; b++)
      i = i * 257 + hashCode(paramArrayOfshort[b]); 
    return i;
  }
  
  public static int hashCode(short[] paramArrayOfshort) {
    if (paramArrayOfshort == null)
      return 0; 
    int i = paramArrayOfshort.length;
    int j;
    for (j = i + 1; --i >= 0; j ^= paramArrayOfshort[i] & 0xFF)
      j *= 257; 
    return j;
  }
  
  public static int hashCode(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return 0; 
    int i = paramArrayOfObject.length;
    int j;
    for (j = i + 1; --i >= 0; j ^= paramArrayOfObject[i].hashCode())
      j *= 257; 
    return j;
  }
  
  public static byte[] clone(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return null; 
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramArrayOfbyte.length);
    return arrayOfByte;
  }
  
  public static char[] clone(char[] paramArrayOfchar) {
    if (paramArrayOfchar == null)
      return null; 
    char[] arrayOfChar = new char[paramArrayOfchar.length];
    System.arraycopy(paramArrayOfchar, 0, arrayOfChar, 0, paramArrayOfchar.length);
    return arrayOfChar;
  }
  
  public static byte[] clone(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null)
      return null; 
    if (paramArrayOfbyte2 == null || paramArrayOfbyte2.length != paramArrayOfbyte1.length)
      return clone(paramArrayOfbyte1); 
    System.arraycopy(paramArrayOfbyte1, 0, paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    return paramArrayOfbyte2;
  }
  
  public static byte[][] clone(byte[][] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return (byte[][])null; 
    byte[][] arrayOfByte = new byte[paramArrayOfbyte.length][];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = clone(paramArrayOfbyte[b]); 
    return arrayOfByte;
  }
  
  public static byte[][][] clone(byte[][][] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return (byte[][][])null; 
    byte[][][] arrayOfByte = new byte[paramArrayOfbyte.length][][];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = clone(paramArrayOfbyte[b]); 
    return arrayOfByte;
  }
  
  public static int[] clone(int[] paramArrayOfint) {
    if (paramArrayOfint == null)
      return null; 
    int[] arrayOfInt = new int[paramArrayOfint.length];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, paramArrayOfint.length);
    return arrayOfInt;
  }
  
  public static long[] clone(long[] paramArrayOflong) {
    if (paramArrayOflong == null)
      return null; 
    long[] arrayOfLong = new long[paramArrayOflong.length];
    System.arraycopy(paramArrayOflong, 0, arrayOfLong, 0, paramArrayOflong.length);
    return arrayOfLong;
  }
  
  public static long[] clone(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    if (paramArrayOflong1 == null)
      return null; 
    if (paramArrayOflong2 == null || paramArrayOflong2.length != paramArrayOflong1.length)
      return clone(paramArrayOflong1); 
    System.arraycopy(paramArrayOflong1, 0, paramArrayOflong2, 0, paramArrayOflong2.length);
    return paramArrayOflong2;
  }
  
  public static short[] clone(short[] paramArrayOfshort) {
    if (paramArrayOfshort == null)
      return null; 
    short[] arrayOfShort = new short[paramArrayOfshort.length];
    System.arraycopy(paramArrayOfshort, 0, arrayOfShort, 0, paramArrayOfshort.length);
    return arrayOfShort;
  }
  
  public static BigInteger[] clone(BigInteger[] paramArrayOfBigInteger) {
    if (paramArrayOfBigInteger == null)
      return null; 
    BigInteger[] arrayOfBigInteger = new BigInteger[paramArrayOfBigInteger.length];
    System.arraycopy(paramArrayOfBigInteger, 0, arrayOfBigInteger, 0, paramArrayOfBigInteger.length);
    return arrayOfBigInteger;
  }
  
  public static byte[] copyOf(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    if (paramInt < paramArrayOfbyte.length) {
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramInt);
    } else {
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramArrayOfbyte.length);
    } 
    return arrayOfByte;
  }
  
  public static char[] copyOf(char[] paramArrayOfchar, int paramInt) {
    char[] arrayOfChar = new char[paramInt];
    if (paramInt < paramArrayOfchar.length) {
      System.arraycopy(paramArrayOfchar, 0, arrayOfChar, 0, paramInt);
    } else {
      System.arraycopy(paramArrayOfchar, 0, arrayOfChar, 0, paramArrayOfchar.length);
    } 
    return arrayOfChar;
  }
  
  public static int[] copyOf(int[] paramArrayOfint, int paramInt) {
    int[] arrayOfInt = new int[paramInt];
    if (paramInt < paramArrayOfint.length) {
      System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, paramInt);
    } else {
      System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, paramArrayOfint.length);
    } 
    return arrayOfInt;
  }
  
  public static long[] copyOf(long[] paramArrayOflong, int paramInt) {
    long[] arrayOfLong = new long[paramInt];
    if (paramInt < paramArrayOflong.length) {
      System.arraycopy(paramArrayOflong, 0, arrayOfLong, 0, paramInt);
    } else {
      System.arraycopy(paramArrayOflong, 0, arrayOfLong, 0, paramArrayOflong.length);
    } 
    return arrayOfLong;
  }
  
  public static BigInteger[] copyOf(BigInteger[] paramArrayOfBigInteger, int paramInt) {
    BigInteger[] arrayOfBigInteger = new BigInteger[paramInt];
    if (paramInt < paramArrayOfBigInteger.length) {
      System.arraycopy(paramArrayOfBigInteger, 0, arrayOfBigInteger, 0, paramInt);
    } else {
      System.arraycopy(paramArrayOfBigInteger, 0, arrayOfBigInteger, 0, paramArrayOfBigInteger.length);
    } 
    return arrayOfBigInteger;
  }
  
  public static byte[] copyOfRange(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = getLength(paramInt1, paramInt2);
    byte[] arrayOfByte = new byte[i];
    if (paramArrayOfbyte.length - paramInt1 < i) {
      System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramArrayOfbyte.length - paramInt1);
    } else {
      System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, i);
    } 
    return arrayOfByte;
  }
  
  public static int[] copyOfRange(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    int i = getLength(paramInt1, paramInt2);
    int[] arrayOfInt = new int[i];
    if (paramArrayOfint.length - paramInt1 < i) {
      System.arraycopy(paramArrayOfint, paramInt1, arrayOfInt, 0, paramArrayOfint.length - paramInt1);
    } else {
      System.arraycopy(paramArrayOfint, paramInt1, arrayOfInt, 0, i);
    } 
    return arrayOfInt;
  }
  
  public static long[] copyOfRange(long[] paramArrayOflong, int paramInt1, int paramInt2) {
    int i = getLength(paramInt1, paramInt2);
    long[] arrayOfLong = new long[i];
    if (paramArrayOflong.length - paramInt1 < i) {
      System.arraycopy(paramArrayOflong, paramInt1, arrayOfLong, 0, paramArrayOflong.length - paramInt1);
    } else {
      System.arraycopy(paramArrayOflong, paramInt1, arrayOfLong, 0, i);
    } 
    return arrayOfLong;
  }
  
  public static BigInteger[] copyOfRange(BigInteger[] paramArrayOfBigInteger, int paramInt1, int paramInt2) {
    int i = getLength(paramInt1, paramInt2);
    BigInteger[] arrayOfBigInteger = new BigInteger[i];
    if (paramArrayOfBigInteger.length - paramInt1 < i) {
      System.arraycopy(paramArrayOfBigInteger, paramInt1, arrayOfBigInteger, 0, paramArrayOfBigInteger.length - paramInt1);
    } else {
      System.arraycopy(paramArrayOfBigInteger, paramInt1, arrayOfBigInteger, 0, i);
    } 
    return arrayOfBigInteger;
  }
  
  private static int getLength(int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0) {
      StringBuffer stringBuffer = new StringBuffer(paramInt1);
      stringBuffer.append(" > ").append(paramInt2);
      throw new IllegalArgumentException(stringBuffer.toString());
    } 
    return i;
  }
  
  public static byte[] append(byte[] paramArrayOfbyte, byte paramByte) {
    if (paramArrayOfbyte == null)
      return new byte[] { paramByte }; 
    int i = paramArrayOfbyte.length;
    byte[] arrayOfByte = new byte[i + 1];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, i);
    arrayOfByte[i] = paramByte;
    return arrayOfByte;
  }
  
  public static short[] append(short[] paramArrayOfshort, short paramShort) {
    if (paramArrayOfshort == null)
      return new short[] { paramShort }; 
    int i = paramArrayOfshort.length;
    short[] arrayOfShort = new short[i + 1];
    System.arraycopy(paramArrayOfshort, 0, arrayOfShort, 0, i);
    arrayOfShort[i] = paramShort;
    return arrayOfShort;
  }
  
  public static int[] append(int[] paramArrayOfint, int paramInt) {
    if (paramArrayOfint == null)
      return new int[] { paramInt }; 
    int i = paramArrayOfint.length;
    int[] arrayOfInt = new int[i + 1];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, i);
    arrayOfInt[i] = paramInt;
    return arrayOfInt;
  }
  
  public static String[] append(String[] paramArrayOfString, String paramString) {
    if (paramArrayOfString == null)
      return new String[] { paramString }; 
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i + 1];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, i);
    arrayOfString[i] = paramString;
    return arrayOfString;
  }
  
  public static byte[] concatenate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 != null && paramArrayOfbyte2 != null) {
      byte[] arrayOfByte = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length];
      System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte1.length);
      System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, paramArrayOfbyte1.length, paramArrayOfbyte2.length);
      return arrayOfByte;
    } 
    return (paramArrayOfbyte2 != null) ? clone(paramArrayOfbyte2) : clone(paramArrayOfbyte1);
  }
  
  public static byte[] concatenate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    if (paramArrayOfbyte1 != null && paramArrayOfbyte2 != null && paramArrayOfbyte3 != null) {
      byte[] arrayOfByte = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length + paramArrayOfbyte3.length];
      System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte1.length);
      System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, paramArrayOfbyte1.length, paramArrayOfbyte2.length);
      System.arraycopy(paramArrayOfbyte3, 0, arrayOfByte, paramArrayOfbyte1.length + paramArrayOfbyte2.length, paramArrayOfbyte3.length);
      return arrayOfByte;
    } 
    return (paramArrayOfbyte1 == null) ? concatenate(paramArrayOfbyte2, paramArrayOfbyte3) : ((paramArrayOfbyte2 == null) ? concatenate(paramArrayOfbyte1, paramArrayOfbyte3) : concatenate(paramArrayOfbyte1, paramArrayOfbyte2));
  }
  
  public static byte[] concatenate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) {
    if (paramArrayOfbyte1 != null && paramArrayOfbyte2 != null && paramArrayOfbyte3 != null && paramArrayOfbyte4 != null) {
      byte[] arrayOfByte = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length + paramArrayOfbyte3.length + paramArrayOfbyte4.length];
      System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte1.length);
      System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, paramArrayOfbyte1.length, paramArrayOfbyte2.length);
      System.arraycopy(paramArrayOfbyte3, 0, arrayOfByte, paramArrayOfbyte1.length + paramArrayOfbyte2.length, paramArrayOfbyte3.length);
      System.arraycopy(paramArrayOfbyte4, 0, arrayOfByte, paramArrayOfbyte1.length + paramArrayOfbyte2.length + paramArrayOfbyte3.length, paramArrayOfbyte4.length);
      return arrayOfByte;
    } 
    return (paramArrayOfbyte4 == null) ? concatenate(paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3) : ((paramArrayOfbyte3 == null) ? concatenate(paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte4) : ((paramArrayOfbyte2 == null) ? concatenate(paramArrayOfbyte1, paramArrayOfbyte3, paramArrayOfbyte4) : concatenate(paramArrayOfbyte2, paramArrayOfbyte3, paramArrayOfbyte4)));
  }
  
  public static byte[] concatenate(byte[][] paramArrayOfbyte) {
    int i = 0;
    for (byte b1 = 0; b1 != paramArrayOfbyte.length; b1++)
      i += (paramArrayOfbyte[b1]).length; 
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    for (byte b2 = 0; b2 != paramArrayOfbyte.length; b2++) {
      System.arraycopy(paramArrayOfbyte[b2], 0, arrayOfByte, j, (paramArrayOfbyte[b2]).length);
      j += (paramArrayOfbyte[b2]).length;
    } 
    return arrayOfByte;
  }
  
  public static int[] concatenate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (paramArrayOfint1 == null)
      return clone(paramArrayOfint2); 
    if (paramArrayOfint2 == null)
      return clone(paramArrayOfint1); 
    int[] arrayOfInt = new int[paramArrayOfint1.length + paramArrayOfint2.length];
    System.arraycopy(paramArrayOfint1, 0, arrayOfInt, 0, paramArrayOfint1.length);
    System.arraycopy(paramArrayOfint2, 0, arrayOfInt, paramArrayOfint1.length, paramArrayOfint2.length);
    return arrayOfInt;
  }
  
  public static byte[] prepend(byte[] paramArrayOfbyte, byte paramByte) {
    if (paramArrayOfbyte == null)
      return new byte[] { paramByte }; 
    int i = paramArrayOfbyte.length;
    byte[] arrayOfByte = new byte[i + 1];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 1, i);
    arrayOfByte[0] = paramByte;
    return arrayOfByte;
  }
  
  public static short[] prepend(short[] paramArrayOfshort, short paramShort) {
    if (paramArrayOfshort == null)
      return new short[] { paramShort }; 
    int i = paramArrayOfshort.length;
    short[] arrayOfShort = new short[i + 1];
    System.arraycopy(paramArrayOfshort, 0, arrayOfShort, 1, i);
    arrayOfShort[0] = paramShort;
    return arrayOfShort;
  }
  
  public static int[] prepend(int[] paramArrayOfint, int paramInt) {
    if (paramArrayOfint == null)
      return new int[] { paramInt }; 
    int i = paramArrayOfint.length;
    int[] arrayOfInt = new int[i + 1];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 1, i);
    arrayOfInt[0] = paramInt;
    return arrayOfInt;
  }
  
  public static byte[] reverse(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return null; 
    byte b = 0;
    int i = paramArrayOfbyte.length;
    byte[] arrayOfByte = new byte[i];
    while (--i >= 0)
      arrayOfByte[i] = paramArrayOfbyte[b++]; 
    return arrayOfByte;
  }
  
  public static int[] reverse(int[] paramArrayOfint) {
    if (paramArrayOfint == null)
      return null; 
    byte b = 0;
    int i = paramArrayOfint.length;
    int[] arrayOfInt = new int[i];
    while (--i >= 0)
      arrayOfInt[i] = paramArrayOfint[b++]; 
    return arrayOfInt;
  }
  
  public static class Iterator<T> implements java.util.Iterator<T> {
    private final T[] dataArray;
    
    private int position = 0;
    
    public Iterator(T[] param1ArrayOfT) {
      this.dataArray = param1ArrayOfT;
    }
    
    public boolean hasNext() {
      return (this.position < this.dataArray.length);
    }
    
    public T next() {
      if (this.position == this.dataArray.length)
        throw new NoSuchElementException("Out of elements: " + this.position); 
      return this.dataArray[this.position++];
    }
    
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove element from an Array.");
    }
  }
}
