package org.bouncycastle.jce.provider;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KDFParameters;

public class BrokenKDF2BytesGenerator implements DerivationFunction {
  private Digest digest;
  
  private byte[] shared;
  
  private byte[] iv;
  
  public BrokenKDF2BytesGenerator(Digest paramDigest) {
    this.digest = paramDigest;
  }
  
  public void init(DerivationParameters paramDerivationParameters) {
    if (!(paramDerivationParameters instanceof KDFParameters))
      throw new IllegalArgumentException("KDF parameters required for generator"); 
    KDFParameters kDFParameters = (KDFParameters)paramDerivationParameters;
    this.shared = kDFParameters.getSharedSecret();
    this.iv = kDFParameters.getIV();
  }
  
  public Digest getDigest() {
    return this.digest;
  }
  
  public int generateBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalArgumentException {
    if (paramArrayOfbyte.length - paramInt2 < paramInt1)
      throw new OutputLengthException("output buffer too small"); 
    long l = paramInt2 * 8L;
    if (l > this.digest.getDigestSize() * 8L * 2147483648L)
      new IllegalArgumentException("Output length to large"); 
    int i = (int)(l / this.digest.getDigestSize());
    byte[] arrayOfByte = null;
    arrayOfByte = new byte[this.digest.getDigestSize()];
    for (byte b = 1; b <= i; b++) {
      this.digest.update(this.shared, 0, this.shared.length);
      this.digest.update((byte)(b & 0xFF));
      this.digest.update((byte)(b >> 8 & 0xFF));
      this.digest.update((byte)(b >> 16 & 0xFF));
      this.digest.update((byte)(b >> 24 & 0xFF));
      this.digest.update(this.iv, 0, this.iv.length);
      this.digest.doFinal(arrayOfByte, 0);
      if (paramInt2 - paramInt1 > arrayOfByte.length) {
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt1, arrayOfByte.length);
        paramInt1 += arrayOfByte.length;
      } else {
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt1, paramInt2 - paramInt1);
      } 
    } 
    this.digest.reset();
    return paramInt2;
  }
}
