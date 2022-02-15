package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

public class XSalsa20Engine extends Salsa20Engine {
  public String getAlgorithmName() {
    return "XSalsa20";
  }
  
  protected int getNonceSize() {
    return 24;
  }
  
  protected void setKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null)
      throw new IllegalArgumentException(getAlgorithmName() + " doesn't support re-init with null key"); 
    if (paramArrayOfbyte1.length != 32)
      throw new IllegalArgumentException(getAlgorithmName() + " requires a 256 bit key"); 
    super.setKey(paramArrayOfbyte1, paramArrayOfbyte2);
    Pack.littleEndianToInt(paramArrayOfbyte2, 8, this.engineState, 8, 2);
    int[] arrayOfInt = new int[this.engineState.length];
    salsaCore(20, this.engineState, arrayOfInt);
    this.engineState[1] = arrayOfInt[0] - this.engineState[0];
    this.engineState[2] = arrayOfInt[5] - this.engineState[5];
    this.engineState[3] = arrayOfInt[10] - this.engineState[10];
    this.engineState[4] = arrayOfInt[15] - this.engineState[15];
    this.engineState[11] = arrayOfInt[6] - this.engineState[6];
    this.engineState[12] = arrayOfInt[7] - this.engineState[7];
    this.engineState[13] = arrayOfInt[8] - this.engineState[8];
    this.engineState[14] = arrayOfInt[9] - this.engineState[9];
    Pack.littleEndianToInt(paramArrayOfbyte2, 16, this.engineState, 6, 2);
  }
}
