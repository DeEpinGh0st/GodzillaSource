package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.MGFParameters;

public class MGF1BytesGenerator implements DerivationFunction {
  private Digest digest;
  
  private byte[] seed;
  
  private int hLen;
  
  public MGF1BytesGenerator(Digest paramDigest) {
    this.digest = paramDigest;
    this.hLen = paramDigest.getDigestSize();
  }
  
  public void init(DerivationParameters paramDerivationParameters) {
    if (!(paramDerivationParameters instanceof MGFParameters))
      throw new IllegalArgumentException("MGF parameters required for MGF1Generator"); 
    MGFParameters mGFParameters = (MGFParameters)paramDerivationParameters;
    this.seed = mGFParameters.getSeed();
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
    byte b = 0;
    this.digest.reset();
    if (paramInt2 > this.hLen)
      do {
        ItoOSP(b, arrayOfByte2);
        this.digest.update(this.seed, 0, this.seed.length);
        this.digest.update(arrayOfByte2, 0, arrayOfByte2.length);
        this.digest.doFinal(arrayOfByte1, 0);
        System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1 + b * this.hLen, this.hLen);
      } while (++b < paramInt2 / this.hLen); 
    if (b * this.hLen < paramInt2) {
      ItoOSP(b, arrayOfByte2);
      this.digest.update(this.seed, 0, this.seed.length);
      this.digest.update(arrayOfByte2, 0, arrayOfByte2.length);
      this.digest.doFinal(arrayOfByte1, 0);
      System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1 + b * this.hLen, paramInt2 - b * this.hLen);
    } 
    return paramInt2;
  }
}
