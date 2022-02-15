package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;

public class OldHMac implements Mac {
  private static final int BLOCK_LENGTH = 64;
  
  private static final byte IPAD = 54;
  
  private static final byte OPAD = 92;
  
  private Digest digest;
  
  private int digestSize;
  
  private byte[] inputPad = new byte[64];
  
  private byte[] outputPad = new byte[64];
  
  public OldHMac(Digest paramDigest) {
    this.digest = paramDigest;
    this.digestSize = paramDigest.getDigestSize();
  }
  
  public String getAlgorithmName() {
    return this.digest.getAlgorithmName() + "/HMAC";
  }
  
  public Digest getUnderlyingDigest() {
    return this.digest;
  }
  
  public void init(CipherParameters paramCipherParameters) {
    this.digest.reset();
    byte[] arrayOfByte = ((KeyParameter)paramCipherParameters).getKey();
    if (arrayOfByte.length > 64) {
      this.digest.update(arrayOfByte, 0, arrayOfByte.length);
      this.digest.doFinal(this.inputPad, 0);
      for (int i = this.digestSize; i < this.inputPad.length; i++)
        this.inputPad[i] = 0; 
    } else {
      System.arraycopy(arrayOfByte, 0, this.inputPad, 0, arrayOfByte.length);
      for (int i = arrayOfByte.length; i < this.inputPad.length; i++)
        this.inputPad[i] = 0; 
    } 
    this.outputPad = new byte[this.inputPad.length];
    System.arraycopy(this.inputPad, 0, this.outputPad, 0, this.inputPad.length);
    byte b;
    for (b = 0; b < this.inputPad.length; b++)
      this.inputPad[b] = (byte)(this.inputPad[b] ^ 0x36); 
    for (b = 0; b < this.outputPad.length; b++)
      this.outputPad[b] = (byte)(this.outputPad[b] ^ 0x5C); 
    this.digest.update(this.inputPad, 0, this.inputPad.length);
  }
  
  public int getMacSize() {
    return this.digestSize;
  }
  
  public void update(byte paramByte) {
    this.digest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = new byte[this.digestSize];
    this.digest.doFinal(arrayOfByte, 0);
    this.digest.update(this.outputPad, 0, this.outputPad.length);
    this.digest.update(arrayOfByte, 0, arrayOfByte.length);
    int i = this.digest.doFinal(paramArrayOfbyte, paramInt);
    reset();
    return i;
  }
  
  public void reset() {
    this.digest.reset();
    this.digest.update(this.inputPad, 0, this.inputPad.length);
  }
}
