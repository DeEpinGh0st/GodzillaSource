package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Grainv1Engine implements StreamCipher {
  private static final int STATE_SIZE = 5;
  
  private byte[] workingKey;
  
  private byte[] workingIV;
  
  private byte[] out;
  
  private int[] lfsr;
  
  private int[] nfsr;
  
  private int output;
  
  private int index = 2;
  
  private boolean initialised = false;
  
  public String getAlgorithmName() {
    return "Grain v1";
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (!(paramCipherParameters instanceof ParametersWithIV))
      throw new IllegalArgumentException("Grain v1 Init parameters must include an IV"); 
    ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
    byte[] arrayOfByte = parametersWithIV.getIV();
    if (arrayOfByte == null || arrayOfByte.length != 8)
      throw new IllegalArgumentException("Grain v1 requires exactly 8 bytes of IV"); 
    if (!(parametersWithIV.getParameters() instanceof KeyParameter))
      throw new IllegalArgumentException("Grain v1 Init parameters must include a key"); 
    KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
    this.workingIV = new byte[(keyParameter.getKey()).length];
    this.workingKey = new byte[(keyParameter.getKey()).length];
    this.lfsr = new int[5];
    this.nfsr = new int[5];
    this.out = new byte[2];
    System.arraycopy(arrayOfByte, 0, this.workingIV, 0, arrayOfByte.length);
    System.arraycopy(keyParameter.getKey(), 0, this.workingKey, 0, (keyParameter.getKey()).length);
    reset();
  }
  
  private void initGrain() {
    for (byte b = 0; b < 10; b++) {
      this.output = getOutput();
      this.nfsr = shift(this.nfsr, getOutputNFSR() ^ this.lfsr[0] ^ this.output);
      this.lfsr = shift(this.lfsr, getOutputLFSR() ^ this.output);
    } 
    this.initialised = true;
  }
  
  private int getOutputNFSR() {
    int i = this.nfsr[0];
    int j = this.nfsr[0] >>> 9 | this.nfsr[1] << 7;
    int k = this.nfsr[0] >>> 14 | this.nfsr[1] << 2;
    int m = this.nfsr[0] >>> 15 | this.nfsr[1] << 1;
    int n = this.nfsr[1] >>> 5 | this.nfsr[2] << 11;
    int i1 = this.nfsr[1] >>> 12 | this.nfsr[2] << 4;
    int i2 = this.nfsr[2] >>> 1 | this.nfsr[3] << 15;
    int i3 = this.nfsr[2] >>> 5 | this.nfsr[3] << 11;
    int i4 = this.nfsr[2] >>> 13 | this.nfsr[3] << 3;
    int i5 = this.nfsr[3] >>> 4 | this.nfsr[4] << 12;
    int i6 = this.nfsr[3] >>> 12 | this.nfsr[4] << 4;
    int i7 = this.nfsr[3] >>> 14 | this.nfsr[4] << 2;
    int i8 = this.nfsr[3] >>> 15 | this.nfsr[4] << 1;
    return (i7 ^ i6 ^ i5 ^ i4 ^ i3 ^ i2 ^ i1 ^ n ^ k ^ j ^ i ^ i8 & i6 ^ i3 & i2 ^ m & j ^ i6 & i5 & i4 ^ i2 & i1 & n ^ i8 & i4 & i1 & j ^ i6 & i5 & i3 & i2 ^ i8 & i6 & n & m ^ i8 & i6 & i5 & i4 & i3 ^ i2 & i1 & n & m & j ^ i5 & i4 & i3 & i2 & i1 & n) & 0xFFFF;
  }
  
  private int getOutputLFSR() {
    int i = this.lfsr[0];
    int j = this.lfsr[0] >>> 13 | this.lfsr[1] << 3;
    int k = this.lfsr[1] >>> 7 | this.lfsr[2] << 9;
    int m = this.lfsr[2] >>> 6 | this.lfsr[3] << 10;
    int n = this.lfsr[3] >>> 3 | this.lfsr[4] << 13;
    int i1 = this.lfsr[3] >>> 14 | this.lfsr[4] << 2;
    return (i ^ j ^ k ^ m ^ n ^ i1) & 0xFFFF;
  }
  
  private int getOutput() {
    int i = this.nfsr[0] >>> 1 | this.nfsr[1] << 15;
    int j = this.nfsr[0] >>> 2 | this.nfsr[1] << 14;
    int k = this.nfsr[0] >>> 4 | this.nfsr[1] << 12;
    int m = this.nfsr[0] >>> 10 | this.nfsr[1] << 6;
    int n = this.nfsr[1] >>> 15 | this.nfsr[2] << 1;
    int i1 = this.nfsr[2] >>> 11 | this.nfsr[3] << 5;
    int i2 = this.nfsr[3] >>> 8 | this.nfsr[4] << 8;
    int i3 = this.nfsr[3] >>> 15 | this.nfsr[4] << 1;
    int i4 = this.lfsr[0] >>> 3 | this.lfsr[1] << 13;
    int i5 = this.lfsr[1] >>> 9 | this.lfsr[2] << 7;
    int i6 = this.lfsr[2] >>> 14 | this.lfsr[3] << 2;
    int i7 = this.lfsr[4];
    return (i5 ^ i3 ^ i4 & i7 ^ i6 & i7 ^ i7 & i3 ^ i4 & i5 & i6 ^ i4 & i6 & i7 ^ i4 & i6 & i3 ^ i5 & i6 & i3 ^ i6 & i7 & i3 ^ i ^ j ^ k ^ m ^ n ^ i1 ^ i2) & 0xFFFF;
  }
  
  private int[] shift(int[] paramArrayOfint, int paramInt) {
    paramArrayOfint[0] = paramArrayOfint[1];
    paramArrayOfint[1] = paramArrayOfint[2];
    paramArrayOfint[2] = paramArrayOfint[3];
    paramArrayOfint[3] = paramArrayOfint[4];
    paramArrayOfint[4] = paramInt;
    return paramArrayOfint;
  }
  
  private void setKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    paramArrayOfbyte2[8] = -1;
    paramArrayOfbyte2[9] = -1;
    this.workingKey = paramArrayOfbyte1;
    this.workingIV = paramArrayOfbyte2;
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.nfsr.length; b2++) {
      this.nfsr[b2] = (this.workingKey[b1 + 1] << 8 | this.workingKey[b1] & 0xFF) & 0xFFFF;
      this.lfsr[b2] = (this.workingIV[b1 + 1] << 8 | this.workingIV[b1] & 0xFF) & 0xFFFF;
      b1 += 2;
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
    this.index = 2;
    setKey(this.workingKey, this.workingIV);
    initGrain();
  }
  
  private void oneRound() {
    this.output = getOutput();
    this.out[0] = (byte)this.output;
    this.out[1] = (byte)(this.output >> 8);
    this.nfsr = shift(this.nfsr, getOutputNFSR() ^ this.lfsr[0]);
    this.lfsr = shift(this.lfsr, getOutputLFSR());
  }
  
  public byte returnByte(byte paramByte) {
    if (!this.initialised)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    return (byte)(paramByte ^ getKeyStream());
  }
  
  private byte getKeyStream() {
    if (this.index > 1) {
      oneRound();
      this.index = 0;
    } 
    return this.out[this.index++];
  }
}
