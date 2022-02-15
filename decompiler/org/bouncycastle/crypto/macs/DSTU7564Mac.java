package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class DSTU7564Mac implements Mac {
  private static final int BITS_IN_BYTE = 8;
  
  private DSTU7564Digest engine;
  
  private int macSize;
  
  private byte[] paddedKey;
  
  private byte[] invertedKey;
  
  private long inputLength;
  
  public DSTU7564Mac(int paramInt) {
    this.engine = new DSTU7564Digest(paramInt);
    this.macSize = paramInt / 8;
    this.paddedKey = null;
    this.invertedKey = null;
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (paramCipherParameters instanceof KeyParameter) {
      byte[] arrayOfByte = ((KeyParameter)paramCipherParameters).getKey();
      this.invertedKey = new byte[arrayOfByte.length];
      this.paddedKey = padKey(arrayOfByte);
      for (byte b = 0; b < this.invertedKey.length; b++)
        this.invertedKey[b] = (byte)(arrayOfByte[b] ^ 0xFFFFFFFF); 
    } else {
      throw new IllegalArgumentException("Bad parameter passed");
    } 
    this.engine.update(this.paddedKey, 0, this.paddedKey.length);
  }
  
  public String getAlgorithmName() {
    return "DSTU7564Mac";
  }
  
  public int getMacSize() {
    return this.macSize;
  }
  
  public void update(byte paramByte) throws IllegalStateException {
    this.engine.update(paramByte);
    this.inputLength++;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramArrayOfbyte.length - paramInt1 < paramInt2)
      throw new DataLengthException("Input buffer too short"); 
    if (this.paddedKey == null)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    this.engine.update(paramArrayOfbyte, paramInt1, paramInt2);
    this.inputLength += paramInt2;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    if (this.paddedKey == null)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    if (paramArrayOfbyte.length - paramInt < this.macSize)
      throw new OutputLengthException("Output buffer too short"); 
    pad();
    this.engine.update(this.invertedKey, 0, this.invertedKey.length);
    this.inputLength = 0L;
    return this.engine.doFinal(paramArrayOfbyte, paramInt);
  }
  
  public void reset() {
    this.inputLength = 0L;
    this.engine.reset();
    if (this.paddedKey != null)
      this.engine.update(this.paddedKey, 0, this.paddedKey.length); 
  }
  
  private void pad() {
    int i = this.engine.getByteLength() - (int)(this.inputLength % this.engine.getByteLength());
    if (i < 13)
      i += this.engine.getByteLength(); 
    byte[] arrayOfByte = new byte[i];
    arrayOfByte[0] = Byte.MIN_VALUE;
    Pack.longToLittleEndian(this.inputLength * 8L, arrayOfByte, arrayOfByte.length - 12);
    this.engine.update(arrayOfByte, 0, arrayOfByte.length);
  }
  
  private byte[] padKey(byte[] paramArrayOfbyte) {
    int i = (paramArrayOfbyte.length + this.engine.getByteLength() - 1) / this.engine.getByteLength() * this.engine.getByteLength();
    int j = this.engine.getByteLength() - paramArrayOfbyte.length % this.engine.getByteLength();
    if (j < 13)
      i += this.engine.getByteLength(); 
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramArrayOfbyte.length);
    arrayOfByte[paramArrayOfbyte.length] = Byte.MIN_VALUE;
    Pack.intToLittleEndian(paramArrayOfbyte.length * 8, arrayOfByte, arrayOfByte.length - 12);
    return arrayOfByte;
  }
}
