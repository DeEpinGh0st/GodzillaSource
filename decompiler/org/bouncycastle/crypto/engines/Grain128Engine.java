package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Grain128Engine implements StreamCipher {
  private static final int STATE_SIZE = 4;
  
  private byte[] workingKey;
  
  private byte[] workingIV;
  
  private byte[] out;
  
  private int[] lfsr;
  
  private int[] nfsr;
  
  private int output;
  
  private int index = 4;
  
  private boolean initialised = false;
  
  public String getAlgorithmName() {
    return "Grain-128";
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (!(paramCipherParameters instanceof ParametersWithIV))
      throw new IllegalArgumentException("Grain-128 Init parameters must include an IV"); 
    ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
    byte[] arrayOfByte = parametersWithIV.getIV();
    if (arrayOfByte == null || arrayOfByte.length != 12)
      throw new IllegalArgumentException("Grain-128  requires exactly 12 bytes of IV"); 
    if (!(parametersWithIV.getParameters() instanceof KeyParameter))
      throw new IllegalArgumentException("Grain-128 Init parameters must include a key"); 
    KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
    this.workingIV = new byte[(keyParameter.getKey()).length];
    this.workingKey = new byte[(keyParameter.getKey()).length];
    this.lfsr = new int[4];
    this.nfsr = new int[4];
    this.out = new byte[4];
    System.arraycopy(arrayOfByte, 0, this.workingIV, 0, arrayOfByte.length);
    System.arraycopy(keyParameter.getKey(), 0, this.workingKey, 0, (keyParameter.getKey()).length);
    reset();
  }
  
  private void initGrain() {
    for (byte b = 0; b < 8; b++) {
      this.output = getOutput();
      this.nfsr = shift(this.nfsr, getOutputNFSR() ^ this.lfsr[0] ^ this.output);
      this.lfsr = shift(this.lfsr, getOutputLFSR() ^ this.output);
    } 
    this.initialised = true;
  }
  
  private int getOutputNFSR() {
    int i = this.nfsr[0];
    int j = this.nfsr[0] >>> 3 | this.nfsr[1] << 29;
    int k = this.nfsr[0] >>> 11 | this.nfsr[1] << 21;
    int m = this.nfsr[0] >>> 13 | this.nfsr[1] << 19;
    int n = this.nfsr[0] >>> 17 | this.nfsr[1] << 15;
    int i1 = this.nfsr[0] >>> 18 | this.nfsr[1] << 14;
    int i2 = this.nfsr[0] >>> 26 | this.nfsr[1] << 6;
    int i3 = this.nfsr[0] >>> 27 | this.nfsr[1] << 5;
    int i4 = this.nfsr[1] >>> 8 | this.nfsr[2] << 24;
    int i5 = this.nfsr[1] >>> 16 | this.nfsr[2] << 16;
    int i6 = this.nfsr[1] >>> 24 | this.nfsr[2] << 8;
    int i7 = this.nfsr[1] >>> 27 | this.nfsr[2] << 5;
    int i8 = this.nfsr[1] >>> 29 | this.nfsr[2] << 3;
    int i9 = this.nfsr[2] >>> 1 | this.nfsr[3] << 31;
    int i10 = this.nfsr[2] >>> 3 | this.nfsr[3] << 29;
    int i11 = this.nfsr[2] >>> 4 | this.nfsr[3] << 28;
    int i12 = this.nfsr[2] >>> 20 | this.nfsr[3] << 12;
    int i13 = this.nfsr[2] >>> 27 | this.nfsr[3] << 5;
    int i14 = this.nfsr[3];
    return i ^ i2 ^ i6 ^ i13 ^ i14 ^ j & i10 ^ k & m ^ n & i1 ^ i3 & i7 ^ i4 & i5 ^ i8 & i9 ^ i11 & i12;
  }
  
  private int getOutputLFSR() {
    int i = this.lfsr[0];
    int j = this.lfsr[0] >>> 7 | this.lfsr[1] << 25;
    int k = this.lfsr[1] >>> 6 | this.lfsr[2] << 26;
    int m = this.lfsr[2] >>> 6 | this.lfsr[3] << 26;
    int n = this.lfsr[2] >>> 17 | this.lfsr[3] << 15;
    int i1 = this.lfsr[3];
    return i ^ j ^ k ^ m ^ n ^ i1;
  }
  
  private int getOutput() {
    int i = this.nfsr[0] >>> 2 | this.nfsr[1] << 30;
    int j = this.nfsr[0] >>> 12 | this.nfsr[1] << 20;
    int k = this.nfsr[0] >>> 15 | this.nfsr[1] << 17;
    int m = this.nfsr[1] >>> 4 | this.nfsr[2] << 28;
    int n = this.nfsr[1] >>> 13 | this.nfsr[2] << 19;
    int i1 = this.nfsr[2];
    int i2 = this.nfsr[2] >>> 9 | this.nfsr[3] << 23;
    int i3 = this.nfsr[2] >>> 25 | this.nfsr[3] << 7;
    int i4 = this.nfsr[2] >>> 31 | this.nfsr[3] << 1;
    int i5 = this.lfsr[0] >>> 8 | this.lfsr[1] << 24;
    int i6 = this.lfsr[0] >>> 13 | this.lfsr[1] << 19;
    int i7 = this.lfsr[0] >>> 20 | this.lfsr[1] << 12;
    int i8 = this.lfsr[1] >>> 10 | this.lfsr[2] << 22;
    int i9 = this.lfsr[1] >>> 28 | this.lfsr[2] << 4;
    int i10 = this.lfsr[2] >>> 15 | this.lfsr[3] << 17;
    int i11 = this.lfsr[2] >>> 29 | this.lfsr[3] << 3;
    int i12 = this.lfsr[2] >>> 31 | this.lfsr[3] << 1;
    return j & i5 ^ i6 & i7 ^ i4 & i8 ^ i9 & i10 ^ j & i4 & i12 ^ i11 ^ i ^ k ^ m ^ n ^ i1 ^ i2 ^ i3;
  }
  
  private int[] shift(int[] paramArrayOfint, int paramInt) {
    paramArrayOfint[0] = paramArrayOfint[1];
    paramArrayOfint[1] = paramArrayOfint[2];
    paramArrayOfint[2] = paramArrayOfint[3];
    paramArrayOfint[3] = paramInt;
    return paramArrayOfint;
  }
  
  private void setKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    paramArrayOfbyte2[12] = -1;
    paramArrayOfbyte2[13] = -1;
    paramArrayOfbyte2[14] = -1;
    paramArrayOfbyte2[15] = -1;
    this.workingKey = paramArrayOfbyte1;
    this.workingIV = paramArrayOfbyte2;
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.nfsr.length; b2++) {
      this.nfsr[b2] = this.workingKey[b1 + 3] << 24 | this.workingKey[b1 + 2] << 16 & 0xFF0000 | this.workingKey[b1 + 1] << 8 & 0xFF00 | this.workingKey[b1] & 0xFF;
      this.lfsr[b2] = this.workingIV[b1 + 3] << 24 | this.workingIV[b1 + 2] << 16 & 0xFF0000 | this.workingIV[b1 + 1] << 8 & 0xFF00 | this.workingIV[b1] & 0xFF;
      b1 += 4;
    } 
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException {
    if (!this.initialised)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    if (paramInt1 + paramInt2 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    for (byte b = 0; b < paramInt2; b++)
      paramArrayOfbyte2[paramInt3 + b] = (byte)(paramArrayOfbyte1[paramInt1 + b] ^ getKeyStream()); 
    return paramInt2;
  }
  
  public void reset() {
    this.index = 4;
    setKey(this.workingKey, this.workingIV);
    initGrain();
  }
  
  private void oneRound() {
    this.output = getOutput();
    this.out[0] = (byte)this.output;
    this.out[1] = (byte)(this.output >> 8);
    this.out[2] = (byte)(this.output >> 16);
    this.out[3] = (byte)(this.output >> 24);
    this.nfsr = shift(this.nfsr, getOutputNFSR() ^ this.lfsr[0]);
    this.lfsr = shift(this.lfsr, getOutputLFSR());
  }
  
  public byte returnByte(byte paramByte) {
    if (!this.initialised)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    return (byte)(paramByte ^ getKeyStream());
  }
  
  private byte getKeyStream() {
    if (this.index > 3) {
      oneRound();
      this.index = 0;
    } 
    return this.out[this.index++];
  }
}
