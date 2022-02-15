package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class VMPCMac implements Mac {
  private byte g;
  
  private byte n = 0;
  
  private byte[] P = null;
  
  private byte s = 0;
  
  private byte[] T;
  
  private byte[] workingIV;
  
  private byte[] workingKey;
  
  private byte x1;
  
  private byte x2;
  
  private byte x3;
  
  private byte x4;
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    byte b1;
    for (b1 = 1; b1 < 25; b1++) {
      this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
      this.x4 = this.P[this.x4 + this.x3 + b1 & 0xFF];
      this.x3 = this.P[this.x3 + this.x2 + b1 & 0xFF];
      this.x2 = this.P[this.x2 + this.x1 + b1 & 0xFF];
      this.x1 = this.P[this.x1 + this.s + b1 & 0xFF];
      this.T[this.g & 0x1F] = (byte)(this.T[this.g & 0x1F] ^ this.x1);
      this.T[this.g + 1 & 0x1F] = (byte)(this.T[this.g + 1 & 0x1F] ^ this.x2);
      this.T[this.g + 2 & 0x1F] = (byte)(this.T[this.g + 2 & 0x1F] ^ this.x3);
      this.T[this.g + 3 & 0x1F] = (byte)(this.T[this.g + 3 & 0x1F] ^ this.x4);
      this.g = (byte)(this.g + 4 & 0x1F);
      byte b = this.P[this.n & 0xFF];
      this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b;
      this.n = (byte)(this.n + 1 & 0xFF);
    } 
    for (b1 = 0; b1 < '̀'; b1++) {
      this.s = this.P[this.s + this.P[b1 & 0xFF] + this.T[b1 & 0x1F] & 0xFF];
      byte b = this.P[b1 & 0xFF];
      this.P[b1 & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b;
    } 
    byte[] arrayOfByte = new byte[20];
    for (byte b2 = 0; b2 < 20; b2++) {
      this.s = this.P[this.s + this.P[b2 & 0xFF] & 0xFF];
      arrayOfByte[b2] = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
      byte b = this.P[b2 & 0xFF];
      this.P[b2 & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b;
    } 
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, arrayOfByte.length);
    reset();
    return arrayOfByte.length;
  }
  
  public String getAlgorithmName() {
    return "VMPC-MAC";
  }
  
  public int getMacSize() {
    return 20;
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (!(paramCipherParameters instanceof ParametersWithIV))
      throw new IllegalArgumentException("VMPC-MAC Init parameters must include an IV"); 
    ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
    KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
    if (!(parametersWithIV.getParameters() instanceof KeyParameter))
      throw new IllegalArgumentException("VMPC-MAC Init parameters must include a key"); 
    this.workingIV = parametersWithIV.getIV();
    if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768)
      throw new IllegalArgumentException("VMPC-MAC requires 1 to 768 bytes of IV"); 
    this.workingKey = keyParameter.getKey();
    reset();
  }
  
  private void initKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.s = 0;
    this.P = new byte[256];
    byte b;
    for (b = 0; b < 'Ā'; b++)
      this.P[b] = (byte)b; 
    for (b = 0; b < '̀'; b++) {
      this.s = this.P[this.s + this.P[b & 0xFF] + paramArrayOfbyte1[b % paramArrayOfbyte1.length] & 0xFF];
      byte b1 = this.P[b & 0xFF];
      this.P[b & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b1;
    } 
    for (b = 0; b < '̀'; b++) {
      this.s = this.P[this.s + this.P[b & 0xFF] + paramArrayOfbyte2[b % paramArrayOfbyte2.length] & 0xFF];
      byte b1 = this.P[b & 0xFF];
      this.P[b & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b1;
    } 
    this.n = 0;
  }
  
  public void reset() {
    initKey(this.workingKey, this.workingIV);
    this.g = this.x1 = this.x2 = this.x3 = this.x4 = this.n = 0;
    this.T = new byte[32];
    for (byte b = 0; b < 32; b++)
      this.T[b] = 0; 
  }
  
  public void update(byte paramByte) throws IllegalStateException {
    this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
    byte b1 = (byte)(paramByte ^ this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF]);
    this.x4 = this.P[this.x4 + this.x3 & 0xFF];
    this.x3 = this.P[this.x3 + this.x2 & 0xFF];
    this.x2 = this.P[this.x2 + this.x1 & 0xFF];
    this.x1 = this.P[this.x1 + this.s + b1 & 0xFF];
    this.T[this.g & 0x1F] = (byte)(this.T[this.g & 0x1F] ^ this.x1);
    this.T[this.g + 1 & 0x1F] = (byte)(this.T[this.g + 1 & 0x1F] ^ this.x2);
    this.T[this.g + 2 & 0x1F] = (byte)(this.T[this.g + 2 & 0x1F] ^ this.x3);
    this.T[this.g + 3 & 0x1F] = (byte)(this.T[this.g + 3 & 0x1F] ^ this.x4);
    this.g = (byte)(this.g + 4 & 0x1F);
    byte b2 = this.P[this.n & 0xFF];
    this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
    this.P[this.s & 0xFF] = b2;
    this.n = (byte)(this.n + 1 & 0xFF);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + paramInt2 > paramArrayOfbyte.length)
      throw new DataLengthException("input buffer too short"); 
    for (byte b = 0; b < paramInt2; b++)
      update(paramArrayOfbyte[paramInt1 + b]); 
  }
}
