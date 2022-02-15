package org.bouncycastle.math.raw;

public abstract class Mont256 {
  private static final long M = 4294967295L;
  
  public static int inverse32(int paramInt) {
    int i = paramInt;
    i *= 2 - paramInt * i;
    i *= 2 - paramInt * i;
    i *= 2 - paramInt * i;
    i *= 2 - paramInt * i;
    return i;
  }
  
  public static void multAdd(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int[] paramArrayOfint4, int paramInt) {
    int i = 0;
    long l = paramArrayOfint2[0] & 0xFFFFFFFFL;
    for (byte b = 0; b < 8; b++) {
      long l1 = paramArrayOfint3[0] & 0xFFFFFFFFL;
      long l2 = paramArrayOfint1[b] & 0xFFFFFFFFL;
      long l3 = l2 * l;
      long l4 = (l3 & 0xFFFFFFFFL) + l1;
      long l5 = ((int)l4 * paramInt) & 0xFFFFFFFFL;
      long l6 = l5 * (paramArrayOfint4[0] & 0xFFFFFFFFL);
      l4 += l6 & 0xFFFFFFFFL;
      l4 = (l4 >>> 32L) + (l3 >>> 32L) + (l6 >>> 32L);
      for (byte b1 = 1; b1 < 8; b1++) {
        l3 = l2 * (paramArrayOfint2[b1] & 0xFFFFFFFFL);
        l6 = l5 * (paramArrayOfint4[b1] & 0xFFFFFFFFL);
        l4 += (l3 & 0xFFFFFFFFL) + (l6 & 0xFFFFFFFFL) + (paramArrayOfint3[b1] & 0xFFFFFFFFL);
        paramArrayOfint3[b1 - 1] = (int)l4;
        l4 = (l4 >>> 32L) + (l3 >>> 32L) + (l6 >>> 32L);
      } 
      l4 += i & 0xFFFFFFFFL;
      paramArrayOfint3[7] = (int)l4;
      i = (int)(l4 >>> 32L);
    } 
    if (i != 0 || Nat256.gte(paramArrayOfint3, paramArrayOfint4))
      Nat256.sub(paramArrayOfint3, paramArrayOfint4, paramArrayOfint3); 
  }
  
  public static void multAddXF(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int[] paramArrayOfint4) {
    int i = 0;
    long l = paramArrayOfint2[0] & 0xFFFFFFFFL;
    for (byte b = 0; b < 8; b++) {
      long l1 = paramArrayOfint1[b] & 0xFFFFFFFFL;
      long l2 = l1 * l + (paramArrayOfint3[0] & 0xFFFFFFFFL);
      long l3 = l2 & 0xFFFFFFFFL;
      l2 = (l2 >>> 32L) + l3;
      for (byte b1 = 1; b1 < 8; b1++) {
        long l4 = l1 * (paramArrayOfint2[b1] & 0xFFFFFFFFL);
        long l5 = l3 * (paramArrayOfint4[b1] & 0xFFFFFFFFL);
        l2 += (l4 & 0xFFFFFFFFL) + (l5 & 0xFFFFFFFFL) + (paramArrayOfint3[b1] & 0xFFFFFFFFL);
        paramArrayOfint3[b1 - 1] = (int)l2;
        l2 = (l2 >>> 32L) + (l4 >>> 32L) + (l5 >>> 32L);
      } 
      l2 += i & 0xFFFFFFFFL;
      paramArrayOfint3[7] = (int)l2;
      i = (int)(l2 >>> 32L);
    } 
    if (i != 0 || Nat256.gte(paramArrayOfint3, paramArrayOfint4))
      Nat256.sub(paramArrayOfint3, paramArrayOfint4, paramArrayOfint3); 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
    for (byte b = 0; b < 8; b++) {
      int i = paramArrayOfint1[0];
      long l1 = (i * paramInt) & 0xFFFFFFFFL;
      long l2 = l1 * (paramArrayOfint2[0] & 0xFFFFFFFFL) + (i & 0xFFFFFFFFL);
      l2 >>>= 32L;
      for (byte b1 = 1; b1 < 8; b1++) {
        l2 += l1 * (paramArrayOfint2[b1] & 0xFFFFFFFFL) + (paramArrayOfint1[b1] & 0xFFFFFFFFL);
        paramArrayOfint1[b1 - 1] = (int)l2;
        l2 >>>= 32L;
      } 
      paramArrayOfint1[7] = (int)l2;
    } 
    if (Nat256.gte(paramArrayOfint1, paramArrayOfint2))
      Nat256.sub(paramArrayOfint1, paramArrayOfint2, paramArrayOfint1); 
  }
  
  public static void reduceXF(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    for (byte b = 0; b < 8; b++) {
      int i = paramArrayOfint1[0];
      long l1 = i & 0xFFFFFFFFL;
      long l2 = l1;
      for (byte b1 = 1; b1 < 8; b1++) {
        l2 += l1 * (paramArrayOfint2[b1] & 0xFFFFFFFFL) + (paramArrayOfint1[b1] & 0xFFFFFFFFL);
        paramArrayOfint1[b1 - 1] = (int)l2;
        l2 >>>= 32L;
      } 
      paramArrayOfint1[7] = (int)l2;
    } 
    if (Nat256.gte(paramArrayOfint1, paramArrayOfint2))
      Nat256.sub(paramArrayOfint1, paramArrayOfint2, paramArrayOfint1); 
  }
}
