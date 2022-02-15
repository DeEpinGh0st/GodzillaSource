package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class RC4Engine implements StreamCipher {
  private static final int STATE_LENGTH = 256;
  
  private byte[] engineState = null;
  
  private int x = 0;
  
  private int y = 0;
  
  private byte[] workingKey = null;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof KeyParameter) {
      this.workingKey = ((KeyParameter)paramCipherParameters).getKey();
      setKey(this.workingKey);
      return;
    } 
    throw new IllegalArgumentException("invalid parameter passed to RC4 init - " + paramCipherParameters.getClass().getName());
  }
  
  public String getAlgorithmName() {
    return "RC4";
  }
  
  public byte returnByte(byte paramByte) {
    this.x = this.x + 1 & 0xFF;
    this.y = this.engineState[this.x] + this.y & 0xFF;
    byte b = this.engineState[this.x];
    this.engineState[this.x] = this.engineState[this.y];
    this.engineState[this.y] = b;
    return (byte)(paramByte ^ this.engineState[this.engineState[this.x] + this.engineState[this.y] & 0xFF]);
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    if (paramInt1 + paramInt2 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    for (byte b = 0; b < paramInt2; b++) {
      this.x = this.x + 1 & 0xFF;
      this.y = this.engineState[this.x] + this.y & 0xFF;
      byte b1 = this.engineState[this.x];
      this.engineState[this.x] = this.engineState[this.y];
      this.engineState[this.y] = b1;
      paramArrayOfbyte2[b + paramInt3] = (byte)(paramArrayOfbyte1[b + paramInt1] ^ this.engineState[this.engineState[this.x] + this.engineState[this.y] & 0xFF]);
    } 
    return paramInt2;
  }
  
  public void reset() {
    setKey(this.workingKey);
  }
  
  private void setKey(byte[] paramArrayOfbyte) {
    this.workingKey = paramArrayOfbyte;
    this.x = 0;
    this.y = 0;
    if (this.engineState == null)
      this.engineState = new byte[256]; 
    int i;
    for (i = 0; i < 256; i++)
      this.engineState[i] = (byte)i; 
    i = 0;
    int j = 0;
    for (byte b = 0; b < 'Ā'; b++) {
      j = (paramArrayOfbyte[i] & 0xFF) + this.engineState[b] + j & 0xFF;
      byte b1 = this.engineState[b];
      this.engineState[b] = this.engineState[j];
      this.engineState[j] = b1;
      i = (i + 1) % paramArrayOfbyte.length;
    } 
  }
}
