package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KDFParameters;

public class ConcatenationKDFGenerator implements DerivationFunction {
  private Digest digest;
  
  private byte[] shared;
  
  private byte[] otherInfo;
  
  private int hLen;
  
  public ConcatenationKDFGenerator(Digest paramDigest) {
    this.digest = paramDigest;
    this.hLen = paramDigest.getDigestSize();
  }
  
  public void init(DerivationParameters paramDerivationParameters) {
    if (paramDerivationParameters instanceof KDFParameters) {
      KDFParameters kDFParameters = (KDFParameters)paramDerivationParameters;
      this.shared = kDFParameters.getSharedSecret();
      this.otherInfo = kDFParameters.getIV();
    } else {
      throw new IllegalArgumentException("KDF parameters required for generator");
    } 
  }
  
  public Digest getDigest() {
    return this.digest;
  }
  
  private void ItoOSP(int paramInt, byte[] paramArrayOfbyte) {
    paramArrayOfbyte[0] = (byte)(paramInt >>> 24);
    paramArrayOfbyte[1] = (byte)(paramInt >>> 16);
    paramArrayOfbyte[2] = (byte)(paramInt >>> 8);
    paramArrayOfbyte[3] = (byte)(paramInt >>> 0);
  }
  
  public int generateBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalArgumentException {
    if (paramArrayOfbyte.length - paramInt2 < paramInt1)
      throw new OutputLengthException("output buffer too small"); 
    byte[] arrayOfByte1 = new byte[this.hLen];
    byte[] arrayOfByte2 = new byte[4];
    byte b = 1;
    int i = 0;
    this.digest.reset();
    if (paramInt2 > this.hLen)
      do {
        ItoOSP(b, arrayOfByte2);
        this.digest.update(arrayOfByte2, 0, arrayOfByte2.length);
        this.digest.update(this.shared, 0, this.shared.length);
        this.digest.update(this.otherInfo, 0, this.otherInfo.length);
        this.digest.doFinal(arrayOfByte1, 0);
        System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1 + i, this.hLen);
        i += this.hLen;
      } while (b++ < paramInt2 / this.hLen); 
    if (i < paramInt2) {
      ItoOSP(b, arrayOfByte2);
      this.digest.update(arrayOfByte2, 0, arrayOfByte2.length);
      this.digest.update(this.shared, 0, this.shared.length);
      this.digest.update(this.otherInfo, 0, this.otherInfo.length);
      this.digest.doFinal(arrayOfByte1, 0);
      System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1 + i, paramInt2 - i);
    } 
    return paramInt2;
  }
}
