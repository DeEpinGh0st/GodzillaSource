package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;

public abstract class GCMUtil {
  private static final int E1 = -520093696;
  
  private static final long E1L = -2233785415175766016L;
  
  private static final int[] LOOKUP = generateLookup();
  
  private static int[] generateLookup() {
    int[] arrayOfInt = new int[256];
    for (byte b = 0; b < 'Ā'; b++) {
      int i = 0;
      for (byte b1 = 7; b1 >= 0; b1--) {
        if ((b & 1 << b1) != 0)
          i ^= -520093696 >>> 7 - b1; 
      } 
      arrayOfInt[b] = i;
    } 
    return arrayOfInt;
  }
  
  public static byte[] oneAsBytes() {
    byte[] arrayOfByte = new byte[16];
    arrayOfByte[0] = Byte.MIN_VALUE;
    return arrayOfByte;
  }
  
  public static int[] oneAsInts() {
    int[] arrayOfInt = new int[4];
    arrayOfInt[0] = Integer.MIN_VALUE;
    return arrayOfInt;
  }
  
  public static long[] oneAsLongs() {
    long[] arrayOfLong = new long[2];
    arrayOfLong[0] = Long.MIN_VALUE;
    return arrayOfLong;
  }
  
  public static byte[] asBytes(int[] paramArrayOfint) {
    byte[] arrayOfByte = new byte[16];
    Pack.intToBigEndian(paramArrayOfint, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void asBytes(int[] paramArrayOfint, byte[] paramArrayOfbyte) {
    Pack.intToBigEndian(paramArrayOfint, paramArrayOfbyte, 0);
  }
  
  public static byte[] asBytes(long[] paramArrayOflong) {
    byte[] arrayOfByte = new byte[16];
    Pack.longToBigEndian(paramArrayOflong, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void asBytes(long[] paramArrayOflong, byte[] paramArrayOfbyte) {
    Pack.longToBigEndian(paramArrayOflong, paramArrayOfbyte, 0);
  }
  
  public static int[] asInts(byte[] paramArrayOfbyte) {
    int[] arrayOfInt = new int[4];
    Pack.bigEndianToInt(paramArrayOfbyte, 0, arrayOfInt);
    return arrayOfInt;
  }
  
  public static void asInts(byte[] paramArrayOfbyte, int[] paramArrayOfint) {
    Pack.bigEndianToInt(paramArrayOfbyte, 0, paramArrayOfint);
  }
  
  public static long[] asLongs(byte[] paramArrayOfbyte) {
    long[] arrayOfLong = new long[2];
    Pack.bigEndianToLong(paramArrayOfbyte, 0, arrayOfLong);
    return arrayOfLong;
  }
  
  public static void asLongs(byte[] paramArrayOfbyte, long[] paramArrayOflong) {
    Pack.bigEndianToLong(paramArrayOfbyte, 0, paramArrayOflong);
  }
  
  public static void multiply(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int[] arrayOfInt1 = asInts(paramArrayOfbyte1);
    int[] arrayOfInt2 = asInts(paramArrayOfbyte2);
    multiply(arrayOfInt1, arrayOfInt2);
    asBytes(arrayOfInt1, paramArrayOfbyte1);
  }
  
  public static void multiply(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = paramArrayOfint1[0];
    int j = paramArrayOfint1[1];
    int k = paramArrayOfint1[2];
    int m = paramArrayOfint1[3];
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    for (byte b = 0; b < 4; b++) {
      int i4 = paramArrayOfint2[b];
      for (byte b1 = 0; b1 < 32; b1++) {
        int i5 = i4 >> 31;
        i4 <<= 1;
        n ^= i & i5;
        i1 ^= j & i5;
        i2 ^= k & i5;
        i3 ^= m & i5;
        int i6 = m << 31 >> 8;
        m = m >>> 1 | k << 31;
        k = k >>> 1 | j << 31;
        j = j >>> 1 | i << 31;
        i = i >>> 1 ^ i6 & 0xE1000000;
      } 
    } 
    paramArrayOfint1[0] = n;
    paramArrayOfint1[1] = i1;
    paramArrayOfint1[2] = i2;
    paramArrayOfint1[3] = i3;
  }
  
  public static void multiply(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    long l3 = 0L;
    long l4 = 0L;
    for (byte b = 0; b < 2; b++) {
      long l = paramArrayOflong2[b];
      for (byte b1 = 0; b1 < 64; b1++) {
        long l5 = l >> 63L;
        l <<= 1L;
        l3 ^= l1 & l5;
        l4 ^= l2 & l5;
        long l6 = l2 << 63L >> 8L;
        l2 = l2 >>> 1L | l1 << 63L;
        l1 = l1 >>> 1L ^ l6 & 0xE100000000000000L;
      } 
    } 
    paramArrayOflong1[0] = l3;
    paramArrayOflong1[1] = l4;
  }
  
  public static void multiplyP(int[] paramArrayOfint) {
    int i = shiftRight(paramArrayOfint) >> 8;
    paramArrayOfint[0] = paramArrayOfint[0] ^ i & 0xE1000000;
  }
  
  public static void multiplyP(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = shiftRight(paramArrayOfint1, paramArrayOfint2) >> 8;
    paramArrayOfint2[0] = paramArrayOfint2[0] ^ i & 0xE1000000;
  }
  
  public static void multiplyP8(int[] paramArrayOfint) {
    int i = shiftRightN(paramArrayOfint, 8);
    paramArrayOfint[0] = paramArrayOfint[0] ^ LOOKUP[i >>> 24];
  }
  
  public static void multiplyP8(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = shiftRightN(paramArrayOfint1, 8, paramArrayOfint2);
    paramArrayOfint2[0] = paramArrayOfint2[0] ^ LOOKUP[i >>> 24];
  }
  
  static int shiftRight(int[] paramArrayOfint) {
    int i = paramArrayOfint[0];
    paramArrayOfint[0] = i >>> 1;
    int j = i << 31;
    i = paramArrayOfint[1];
    paramArrayOfint[1] = i >>> 1 | j;
    j = i << 31;
    i = paramArrayOfint[2];
    paramArrayOfint[2] = i >>> 1 | j;
    j = i << 31;
    i = paramArrayOfint[3];
    paramArrayOfint[3] = i >>> 1 | j;
    return i << 31;
  }
  
  static int shiftRight(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = paramArrayOfint1[0];
    paramArrayOfint2[0] = i >>> 1;
    int j = i << 31;
    i = paramArrayOfint1[1];
    paramArrayOfint2[1] = i >>> 1 | j;
    j = i << 31;
    i = paramArrayOfint1[2];
    paramArrayOfint2[2] = i >>> 1 | j;
    j = i << 31;
    i = paramArrayOfint1[3];
    paramArrayOfint2[3] = i >>> 1 | j;
    return i << 31;
  }
  
  static long shiftRight(long[] paramArrayOflong) {
    long l1 = paramArrayOflong[0];
    paramArrayOflong[0] = l1 >>> 1L;
    long l2 = l1 << 63L;
    l1 = paramArrayOflong[1];
    paramArrayOflong[1] = l1 >>> 1L | l2;
    return l1 << 63L;
  }
  
  static long shiftRight(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[0];
    paramArrayOflong2[0] = l1 >>> 1L;
    long l2 = l1 << 63L;
    l1 = paramArrayOflong1[1];
    paramArrayOflong2[1] = l1 >>> 1L | l2;
    return l1 << 63L;
  }
  
  static int shiftRightN(int[] paramArrayOfint, int paramInt) {
    int i = paramArrayOfint[0];
    int j = 32 - paramInt;
    paramArrayOfint[0] = i >>> paramInt;
    int k = i << j;
    i = paramArrayOfint[1];
    paramArrayOfint[1] = i >>> paramInt | k;
    k = i << j;
    i = paramArrayOfint[2];
    paramArrayOfint[2] = i >>> paramInt | k;
    k = i << j;
    i = paramArrayOfint[3];
    paramArrayOfint[3] = i >>> paramInt | k;
    return i << j;
  }
  
  static int shiftRightN(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2) {
    int i = paramArrayOfint1[0];
    int j = 32 - paramInt;
    paramArrayOfint2[0] = i >>> paramInt;
    int k = i << j;
    i = paramArrayOfint1[1];
    paramArrayOfint2[1] = i >>> paramInt | k;
    k = i << j;
    i = paramArrayOfint1[2];
    paramArrayOfint2[2] = i >>> paramInt | k;
    k = i << j;
    i = paramArrayOfint1[3];
    paramArrayOfint2[3] = i >>> paramInt | k;
    return i << j;
  }
  
  public static void xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte b = 0;
    do {
      paramArrayOfbyte1[b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]);
      paramArrayOfbyte1[++b] = (byte)(paramArrayOfbyte1[++b] ^ paramArrayOfbyte2[b]);
      paramArrayOfbyte1[++b] = (byte)(paramArrayOfbyte1[++b] ^ paramArrayOfbyte2[b]);
      paramArrayOfbyte1[++b] = (byte)(paramArrayOfbyte1[++b] ^ paramArrayOfbyte2[b]);
    } while (++b < 16);
  }
  
  public static void xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
    while (--paramInt2 >= 0)
      paramArrayOfbyte1[paramInt2] = (byte)(paramArrayOfbyte1[paramInt2] ^ paramArrayOfbyte2[paramInt1 + paramInt2]); 
  }
  
  public static void xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    byte b = 0;
    do {
      paramArrayOfbyte3[b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]);
      paramArrayOfbyte3[++b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]);
      paramArrayOfbyte3[++b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]);
      paramArrayOfbyte3[++b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]);
    } while (++b < 16);
  }
  
  public static void xor(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    paramArrayOfint1[0] = paramArrayOfint1[0] ^ paramArrayOfint2[0];
    paramArrayOfint1[1] = paramArrayOfint1[1] ^ paramArrayOfint2[1];
    paramArrayOfint1[2] = paramArrayOfint1[2] ^ paramArrayOfint2[2];
    paramArrayOfint1[3] = paramArrayOfint1[3] ^ paramArrayOfint2[3];
  }
  
  public static void xor(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    paramArrayOfint3[0] = paramArrayOfint1[0] ^ paramArrayOfint2[0];
    paramArrayOfint3[1] = paramArrayOfint1[1] ^ paramArrayOfint2[1];
    paramArrayOfint3[2] = paramArrayOfint1[2] ^ paramArrayOfint2[2];
    paramArrayOfint3[3] = paramArrayOfint1[3] ^ paramArrayOfint2[3];
  }
  
  public static void xor(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong1[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong1[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
  }
  
  public static void xor(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
  }
}
