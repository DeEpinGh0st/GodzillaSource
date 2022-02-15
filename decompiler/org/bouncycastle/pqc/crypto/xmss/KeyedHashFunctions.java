package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;

final class KeyedHashFunctions {
  private final Digest digest;
  
  private final int digestSize;
  
  protected KeyedHashFunctions(Digest paramDigest, int paramInt) {
    if (paramDigest == null)
      throw new NullPointerException("digest == null"); 
    this.digest = paramDigest;
    this.digestSize = paramInt;
  }
  
  private byte[] coreDigest(int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte1 = XMSSUtil.toBytesBigEndian(paramInt, this.digestSize);
    this.digest.update(arrayOfByte1, 0, arrayOfByte1.length);
    this.digest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    this.digest.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    byte[] arrayOfByte2 = new byte[this.digestSize];
    if (this.digest instanceof Xof) {
      ((Xof)this.digest).doFinal(arrayOfByte2, 0, this.digestSize);
    } else {
      this.digest.doFinal(arrayOfByte2, 0);
    } 
    return arrayOfByte2;
  }
  
  protected byte[] F(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1.length != this.digestSize)
      throw new IllegalArgumentException("wrong key length"); 
    if (paramArrayOfbyte2.length != this.digestSize)
      throw new IllegalArgumentException("wrong in length"); 
    return coreDigest(0, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  protected byte[] H(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1.length != this.digestSize)
      throw new IllegalArgumentException("wrong key length"); 
    if (paramArrayOfbyte2.length != 2 * this.digestSize)
      throw new IllegalArgumentException("wrong in length"); 
    return coreDigest(1, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  protected byte[] HMsg(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1.length != 3 * this.digestSize)
      throw new IllegalArgumentException("wrong key length"); 
    return coreDigest(2, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  protected byte[] PRF(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1.length != this.digestSize)
      throw new IllegalArgumentException("wrong key length"); 
    if (paramArrayOfbyte2.length != 32)
      throw new IllegalArgumentException("wrong address length"); 
    return coreDigest(3, paramArrayOfbyte1, paramArrayOfbyte2);
  }
}
