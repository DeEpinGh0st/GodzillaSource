package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class VMPCEngine implements StreamCipher {
  protected byte n = 0;
  
  protected byte[] P = null;
  
  protected byte s = 0;
  
  protected byte[] workingIV;
  
  protected byte[] workingKey;
  
  public String getAlgorithmName() {
    return "VMPC";
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof ParametersWithIV))
      throw new IllegalArgumentException("VMPC init parameters must include an IV"); 
    ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
    if (!(parametersWithIV.getParameters() instanceof KeyParameter))
      throw new IllegalArgumentException("VMPC init parameters must include a key"); 
    KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
    this.workingIV = parametersWithIV.getIV();
    if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768)
      throw new IllegalArgumentException("VMPC requires 1 to 768 bytes of IV"); 
    this.workingKey = keyParameter.getKey();
    initKey(this.workingKey, this.workingIV);
  }
  
  protected void initKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
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
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    if (paramInt1 + paramInt2 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    for (byte b = 0; b < paramInt2; b++) {
      this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
      byte b1 = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
      byte b2 = this.P[this.n & 0xFF];
      this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b2;
      this.n = (byte)(this.n + 1 & 0xFF);
      paramArrayOfbyte2[b + paramInt3] = (byte)(paramArrayOfbyte1[b + paramInt1] ^ b1);
    } 
    return paramInt2;
  }
  
  public void reset() {
    initKey(this.workingKey, this.workingIV);
  }
  
  public byte returnByte(byte paramByte) {
    this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
    byte b1 = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
    byte b2 = this.P[this.n & 0xFF];
    this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
    this.P[this.s & 0xFF] = b2;
    this.n = (byte)(this.n + 1 & 0xFF);
    return (byte)(paramByte ^ b1);
  }
}
