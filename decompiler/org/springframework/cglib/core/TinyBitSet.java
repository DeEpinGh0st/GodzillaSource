package org.springframework.cglib.core;















@Deprecated
public class TinyBitSet
{
  private static int[] T = new int[256];
  private int value = 0;
  
  private static int gcount(int x) {
    int c = 0;
    while (x != 0) {
      c++;
      x &= x - 1;
    } 
    return c;
  }
  
  static {
    for (int j = 0; j < 256; j++) {
      T[j] = gcount(j);
    }
  }
  
  private static int topbit(int i) {
    int j;
    for (j = 0; i != 0; i ^= j) {
      j = i & -i;
    }
    return j;
  }
  
  private static int log2(int i) {
    int j = 0;
    for (j = 0; i != 0; i >>= 1) {
      j++;
    }
    return j;
  }
  
  public int length() {
    return log2(topbit(this.value));
  }





  
  public int cardinality() {
    int w = this.value;
    int c = 0;
    while (w != 0) {
      c += T[w & 0xFF];
      w >>= 8;
    } 
    return c;
  }
  
  public boolean get(int index) {
    return ((this.value & 1 << index) != 0);
  }
  
  public void set(int index) {
    this.value |= 1 << index;
  }
  
  public void clear(int index) {
    this.value &= 1 << index ^ 0xFFFFFFFF;
  }
}
