package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Pack;

public class BaseKDFBytesGenerator implements DigestDerivationFunction {
  private int counterStart;
  
  private Digest digest;
  
  private byte[] shared;
  
  private byte[] iv;
  
  protected BaseKDFBytesGenerator(int paramInt, Digest paramDigest) {
    this.counterStart = paramInt;
    this.digest = paramDigest;
  }
  
  public void init(DerivationParameters paramDerivationParameters) {
    if (paramDerivationParameters instanceof KDFParameters) {
      KDFParameters kDFParameters = (KDFParameters)paramDerivationParameters;
      this.shared = kDFParameters.getSharedSecret();
      this.iv = kDFParameters.getIV();
    } else if (paramDerivationParameters instanceof ISO18033KDFParameters) {
      ISO18033KDFParameters iSO18033KDFParameters = (ISO18033KDFParameters)paramDerivationParameters;
      this.shared = iSO18033KDFParameters.getSeed();
      this.iv = null;
    } else {
      throw new IllegalArgumentException("KDF parameters required for generator");
    } 
  }
  
  public Digest getDigest() {
    return this.digest;
  }
  
  public int generateBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalArgumentException {
    if (paramArrayOfbyte.length - paramInt2 < paramInt1)
      throw new OutputLengthException("output buffer too small"); 
    long l = paramInt2;
    int i = this.digest.getDigestSize();
    if (l > 8589934591L)
      throw new IllegalArgumentException("Output length too large"); 
    int j = (int)((l + i - 1L) / i);
    byte[] arrayOfByte1 = new byte[this.digest.getDigestSize()];
    byte[] arrayOfByte2 = new byte[4];
    Pack.intToBigEndian(this.counterStart, arrayOfByte2, 0);
    int k = this.counterStart & 0xFFFFFF00;
    for (byte b = 0; b < j; b++) {
      this.digest.update(this.shared, 0, this.shared.length);
      this.digest.update(arrayOfByte2, 0, arrayOfByte2.length);
      if (this.iv != null)
        this.digest.update(this.iv, 0, this.iv.length); 
      this.digest.doFinal(arrayOfByte1, 0);
      if (paramInt2 > i) {
        System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1, i);
        paramInt1 += i;
        paramInt2 -= i;
      } else {
        System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1, paramInt2);
      } 
      arrayOfByte2[3] = (byte)(arrayOfByte2[3] + 1);
      if ((byte)(arrayOfByte2[3] + 1) == 0) {
        k += 256;
        Pack.intToBigEndian(k, arrayOfByte2, 0);
      } 
    } 
    this.digest.reset();
    return (int)l;
  }
}
