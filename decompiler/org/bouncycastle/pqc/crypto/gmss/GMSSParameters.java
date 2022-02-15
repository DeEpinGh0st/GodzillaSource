package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.util.Arrays;

public class GMSSParameters {
  private int numOfLayers;
  
  private int[] heightOfTrees;
  
  private int[] winternitzParameter;
  
  private int[] K;
  
  public GMSSParameters(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) throws IllegalArgumentException {
    init(paramInt, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
  }
  
  private void init(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) throws IllegalArgumentException {
    boolean bool = true;
    String str = "";
    this.numOfLayers = paramInt;
    if (this.numOfLayers != paramArrayOfint2.length || this.numOfLayers != paramArrayOfint1.length || this.numOfLayers != paramArrayOfint3.length) {
      bool = false;
      str = "Unexpected parameterset format";
    } 
    for (byte b = 0; b < this.numOfLayers; b++) {
      if (paramArrayOfint3[b] < 2 || (paramArrayOfint1[b] - paramArrayOfint3[b]) % 2 != 0) {
        bool = false;
        str = "Wrong parameter K (K >= 2 and H-K even required)!";
      } 
      if (paramArrayOfint1[b] < 4 || paramArrayOfint2[b] < 2) {
        bool = false;
        str = "Wrong parameter H or w (H > 3 and w > 1 required)!";
      } 
    } 
    if (bool) {
      this.heightOfTrees = Arrays.clone(paramArrayOfint1);
      this.winternitzParameter = Arrays.clone(paramArrayOfint2);
      this.K = Arrays.clone(paramArrayOfint3);
    } else {
      throw new IllegalArgumentException(str);
    } 
  }
  
  public GMSSParameters(int paramInt) throws IllegalArgumentException {
    if (paramInt <= 10) {
      int[] arrayOfInt1 = { 10 };
      int[] arrayOfInt2 = { 3 };
      int[] arrayOfInt3 = { 2 };
      init(arrayOfInt1.length, arrayOfInt1, arrayOfInt2, arrayOfInt3);
    } else if (paramInt <= 20) {
      int[] arrayOfInt1 = { 10, 10 };
      int[] arrayOfInt2 = { 5, 4 };
      int[] arrayOfInt3 = { 2, 2 };
      init(arrayOfInt1.length, arrayOfInt1, arrayOfInt2, arrayOfInt3);
    } else {
      int[] arrayOfInt1 = { 10, 10, 10, 10 };
      int[] arrayOfInt2 = { 9, 9, 9, 3 };
      int[] arrayOfInt3 = { 2, 2, 2, 2 };
      init(arrayOfInt1.length, arrayOfInt1, arrayOfInt2, arrayOfInt3);
    } 
  }
  
  public int getNumOfLayers() {
    return this.numOfLayers;
  }
  
  public int[] getHeightOfTrees() {
    return Arrays.clone(this.heightOfTrees);
  }
  
  public int[] getWinternitzParameter() {
    return Arrays.clone(this.winternitzParameter);
  }
  
  public int[] getK() {
    return Arrays.clone(this.K);
  }
}
