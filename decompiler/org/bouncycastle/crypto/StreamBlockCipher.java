package org.bouncycastle.crypto;

public abstract class StreamBlockCipher implements BlockCipher, StreamCipher {
  private final BlockCipher cipher;
  
  protected StreamBlockCipher(BlockCipher paramBlockCipher) {
    this.cipher = paramBlockCipher;
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.cipher;
  }
  
  public final byte returnByte(byte paramByte) {
    return calculateByte(paramByte);
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException {
    if (paramInt1 + paramInt2 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too small"); 
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    int k = paramInt3;
    while (i < j)
      paramArrayOfbyte2[k++] = calculateByte(paramArrayOfbyte1[i++]); 
    return paramInt2;
  }
  
  protected abstract byte calculateByte(byte paramByte);
}
