package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class ISAACEngine implements StreamCipher {
  private final int sizeL = 8;
  
  private final int stateArraySize = 256;
  
  private int[] engineState = null;
  
  private int[] results = null;
  
  private int a = 0;
  
  private int b = 0;
  
  private int c = 0;
  
  private int index = 0;
  
  private byte[] keyStream = new byte[1024];
  
  private byte[] workingKey = null;
  
  private boolean initialised = false;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("invalid parameter passed to ISAAC init - " + paramCipherParameters.getClass().getName()); 
    KeyParameter keyParameter = (KeyParameter)paramCipherParameters;
    setKey(keyParameter.getKey());
  }
  
  public byte returnByte(byte paramByte) {
    if (this.index == 0) {
      isaac();
      this.keyStream = Pack.intToBigEndian(this.results);
    } 
    byte b = (byte)(this.keyStream[this.index] ^ paramByte);
    this.index = this.index + 1 & 0x3FF;
    return b;
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    if (!this.initialised)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    if (paramInt1 + paramInt2 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    for (byte b = 0; b < paramInt2; b++) {
      if (this.index == 0) {
        isaac();
        this.keyStream = Pack.intToBigEndian(this.results);
      } 
      paramArrayOfbyte2[b + paramInt3] = (byte)(this.keyStream[this.index] ^ paramArrayOfbyte1[b + paramInt1]);
      this.index = this.index + 1 & 0x3FF;
    } 
    return paramInt2;
  }
  
  public String getAlgorithmName() {
    return "ISAAC";
  }
  
  public void reset() {
    setKey(this.workingKey);
  }
  
  private void setKey(byte[] paramArrayOfbyte) {
    this.workingKey = paramArrayOfbyte;
    if (this.engineState == null)
      this.engineState = new int[256]; 
    if (this.results == null)
      this.results = new int[256]; 
    byte b;
    for (b = 0; b < 'Ā'; b++) {
      this.results[b] = 0;
      this.engineState[b] = 0;
    } 
    this.a = this.b = this.c = 0;
    this.index = 0;
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length + (paramArrayOfbyte.length & 0x3)];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramArrayOfbyte.length);
    for (b = 0; b < arrayOfByte.length; b += 4)
      this.results[b >>> 2] = Pack.littleEndianToInt(arrayOfByte, b); 
    int[] arrayOfInt = new int[8];
    for (b = 0; b < 8; b++)
      arrayOfInt[b] = -1640531527; 
    for (b = 0; b < 4; b++)
      mix(arrayOfInt); 
    for (b = 0; b < 2; b++) {
      for (byte b1 = 0; b1 < 'Ā'; b1 += 8) {
        byte b2;
        for (b2 = 0; b2 < 8; b2++)
          arrayOfInt[b2] = arrayOfInt[b2] + ((b < 1) ? this.results[b1 + b2] : this.engineState[b1 + b2]); 
        mix(arrayOfInt);
        for (b2 = 0; b2 < 8; b2++)
          this.engineState[b1 + b2] = arrayOfInt[b2]; 
      } 
    } 
    isaac();
    this.initialised = true;
  }
  
  private void isaac() {
    this.b += ++this.c;
    for (byte b = 0; b < 'Ā'; b++) {
      int i = this.engineState[b];
      switch (b & 0x3) {
        case 0:
          this.a ^= this.a << 13;
          break;
        case 1:
          this.a ^= this.a >>> 6;
          break;
        case 2:
          this.a ^= this.a << 2;
          break;
        case 3:
          this.a ^= this.a >>> 16;
          break;
      } 
      this.a += this.engineState[b + 128 & 0xFF];
      int j = this.engineState[i >>> 2 & 0xFF] + this.a + this.b;
      this.results[b] = this.b = this.engineState[j >>> 10 & 0xFF] + i;
    } 
  }
  
  private void mix(int[] paramArrayOfint) {
    paramArrayOfint[0] = paramArrayOfint[0] ^ paramArrayOfint[1] << 11;
    paramArrayOfint[3] = paramArrayOfint[3] + paramArrayOfint[0];
    paramArrayOfint[1] = paramArrayOfint[1] + paramArrayOfint[2];
    paramArrayOfint[1] = paramArrayOfint[1] ^ paramArrayOfint[2] >>> 2;
    paramArrayOfint[4] = paramArrayOfint[4] + paramArrayOfint[1];
    paramArrayOfint[2] = paramArrayOfint[2] + paramArrayOfint[3];
    paramArrayOfint[2] = paramArrayOfint[2] ^ paramArrayOfint[3] << 8;
    paramArrayOfint[5] = paramArrayOfint[5] + paramArrayOfint[2];
    paramArrayOfint[3] = paramArrayOfint[3] + paramArrayOfint[4];
    paramArrayOfint[3] = paramArrayOfint[3] ^ paramArrayOfint[4] >>> 16;
    paramArrayOfint[6] = paramArrayOfint[6] + paramArrayOfint[3];
    paramArrayOfint[4] = paramArrayOfint[4] + paramArrayOfint[5];
    paramArrayOfint[4] = paramArrayOfint[4] ^ paramArrayOfint[5] << 10;
    paramArrayOfint[7] = paramArrayOfint[7] + paramArrayOfint[4];
    paramArrayOfint[5] = paramArrayOfint[5] + paramArrayOfint[6];
    paramArrayOfint[5] = paramArrayOfint[5] ^ paramArrayOfint[6] >>> 4;
    paramArrayOfint[0] = paramArrayOfint[0] + paramArrayOfint[5];
    paramArrayOfint[6] = paramArrayOfint[6] + paramArrayOfint[7];
    paramArrayOfint[6] = paramArrayOfint[6] ^ paramArrayOfint[7] << 8;
    paramArrayOfint[1] = paramArrayOfint[1] + paramArrayOfint[6];
    paramArrayOfint[7] = paramArrayOfint[7] + paramArrayOfint[0];
    paramArrayOfint[7] = paramArrayOfint[7] ^ paramArrayOfint[0] >>> 9;
    paramArrayOfint[2] = paramArrayOfint[2] + paramArrayOfint[7];
    paramArrayOfint[0] = paramArrayOfint[0] + paramArrayOfint[1];
  }
}
