package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class Tables64kGCMMultiplier implements GCMMultiplier {
  private byte[] H;
  
  private int[][][] M;
  
  public void init(byte[] paramArrayOfbyte) {
    if (this.M == null) {
      this.M = new int[16][256][4];
    } else if (Arrays.areEqual(this.H, paramArrayOfbyte)) {
      return;
    } 
    this.H = Arrays.clone(paramArrayOfbyte);
    GCMUtil.asInts(paramArrayOfbyte, this.M[0][128]);
    int i;
    for (i = 64; i >= 1; i >>= 1)
      GCMUtil.multiplyP(this.M[0][i + i], this.M[0][i]); 
    i = 0;
    while (true) {
      int j;
      for (j = 2; j < 256; j += j) {
        for (byte b = 1; b < j; b++)
          GCMUtil.xor(this.M[i][j], this.M[i][b], this.M[i][j + b]); 
      } 
      if (++i == 16)
        return; 
      for (j = 128; j > 0; j >>= 1)
        GCMUtil.multiplyP8(this.M[i - 1][j], this.M[i][j]); 
    } 
  }
  
  public void multiplyH(byte[] paramArrayOfbyte) {
    int[] arrayOfInt = new int[4];
    for (byte b = 15; b >= 0; b--) {
      int[] arrayOfInt1 = this.M[b][paramArrayOfbyte[b] & 0xFF];
      arrayOfInt[0] = arrayOfInt[0] ^ arrayOfInt1[0];
      arrayOfInt[1] = arrayOfInt[1] ^ arrayOfInt1[1];
      arrayOfInt[2] = arrayOfInt[2] ^ arrayOfInt1[2];
      arrayOfInt[3] = arrayOfInt[3] ^ arrayOfInt1[3];
    } 
    Pack.intToBigEndian(arrayOfInt, paramArrayOfbyte, 0);
  }
}
