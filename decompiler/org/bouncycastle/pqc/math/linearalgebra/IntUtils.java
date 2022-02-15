package org.bouncycastle.pqc.math.linearalgebra;

public final class IntUtils {
  public static boolean equals(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (paramArrayOfint1.length != paramArrayOfint2.length)
      return false; 
    int i = 1;
    for (int j = paramArrayOfint1.length - 1; j >= 0; j--)
      i &= (paramArrayOfint1[j] == paramArrayOfint2[j]) ? 1 : 0; 
    return i;
  }
  
  public static int[] clone(int[] paramArrayOfint) {
    int[] arrayOfInt = new int[paramArrayOfint.length];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, paramArrayOfint.length);
    return arrayOfInt;
  }
  
  public static void fill(int[] paramArrayOfint, int paramInt) {
    for (int i = paramArrayOfint.length - 1; i >= 0; i--)
      paramArrayOfint[i] = paramInt; 
  }
  
  public static void quicksort(int[] paramArrayOfint) {
    quicksort(paramArrayOfint, 0, paramArrayOfint.length - 1);
  }
  
  public static void quicksort(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    if (paramInt2 > paramInt1) {
      int i = partition(paramArrayOfint, paramInt1, paramInt2, paramInt2);
      quicksort(paramArrayOfint, paramInt1, i - 1);
      quicksort(paramArrayOfint, i + 1, paramInt2);
    } 
  }
  
  private static int partition(int[] paramArrayOfint, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramArrayOfint[paramInt3];
    paramArrayOfint[paramInt3] = paramArrayOfint[paramInt2];
    paramArrayOfint[paramInt2] = i;
    int j = paramInt1;
    int k;
    for (k = paramInt1; k < paramInt2; k++) {
      if (paramArrayOfint[k] <= i) {
        int m = paramArrayOfint[j];
        paramArrayOfint[j] = paramArrayOfint[k];
        paramArrayOfint[k] = m;
        j++;
      } 
    } 
    k = paramArrayOfint[j];
    paramArrayOfint[j] = paramArrayOfint[paramInt2];
    paramArrayOfint[paramInt2] = k;
    return j;
  }
  
  public static int[] subArray(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    int[] arrayOfInt = new int[paramInt2 - paramInt1];
    System.arraycopy(paramArrayOfint, paramInt1, arrayOfInt, 0, paramInt2 - paramInt1);
    return arrayOfInt;
  }
  
  public static String toString(int[] paramArrayOfint) {
    String str = "";
    for (byte b = 0; b < paramArrayOfint.length; b++)
      str = str + paramArrayOfint[b] + " "; 
    return str;
  }
  
  public static String toHexString(int[] paramArrayOfint) {
    return ByteUtils.toHexString(BigEndianConversions.toByteArray(paramArrayOfint));
  }
}
